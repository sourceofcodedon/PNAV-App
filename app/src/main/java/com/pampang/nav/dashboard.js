
import { auth, db } from './firebase-config.js';
import { onAuthStateChanged, signOut } from 'https://www.gstatic.com/firebasejs/10.7.1/firebase-auth.js';
import {
    collection,
    getDocs,
    query,
    where,
    orderBy,
    doc,
    addDoc,
    getDoc,
    updateDoc,
    deleteDoc,
    setDoc,
    Timestamp
} from 'https://www.gstatic.com/firebasejs/10.7.1/firebase-firestore.js';

// Global variables
let allUsers = [];
let allAdmins = [];
let allStores = [];
let allNavigationHistory = [];
let pendingUsers = [];
let currentView = 'dashboard';
let visitedShopsChart = null;

// Navigation elements
const navItems = document.querySelectorAll('nav a');
const pageTitle = document.getElementById('pageTitle');
const dashboardContent = document.getElementById('dashboardContent');
const usersContent = document.getElementById('usersContent');
const adminContent = document.getElementById('adminContent');
const pendingRegistrationContent = document.getElementById('pendingRegistrationContent');
const storesContent = document.getElementById('storesContent');
const usersHistoryContent = document.getElementById('usersHistoryContent');
const logsContent = document.getElementById('logsContent');

// Check authentication state
onAuthStateChanged(auth, async (user) => {
    if (user) {
        // Get user data from Firestore to check role
        const userDocRef = doc(db, "users", user.uid);
        const userDocSnap = await getDoc(userDocRef);

        if (userDocSnap.exists() && userDocSnap.data().role === 'admin') {
            // User is an admin, proceed to load dashboard
            console.log('Admin user is logged in:', user.uid);
            document.getElementById('userEmail').textContent = user.email;

            const userData = userDocSnap.data();
            const profilePictureUrl = userData.profilePicture;
            const userImage = document.getElementById('userImage');

            if (profilePictureUrl) {
                userImage.src = profilePictureUrl;
            } else {
                userImage.innerHTML = `
                    <svg class="w-6 h-6 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
                    </svg>
                `;
            }

            await loadDashboardData();
            setupNavigation();
            await loadNavigationHistory(); // Load navigation history on initial dashboard load
        } else {
            // Not an admin or user document doesn't exist, sign out and redirect
            logAction('User is not an admin or data not found, signing out...');
            await signOut(auth);
            window.location.href = 'index.html';
        }    } else {
        logAction('No user logged in, redirecting to login...');
        window.location.href = 'index.html';
    }
});

// Setup navigation functionality
function setupNavigation() {
    // Navigation item clicks
    navItems.forEach(item => {
        item.addEventListener('click', (e) => {
            if (item.getAttribute('href') === 'javascript:void(0)') {
                e.preventDefault();
                const view = item.querySelector('span').textContent.toLowerCase().replace(' ', '');
                switchView(view);
            }
        });
    });

    // Users card click
    const usersCard = document.getElementById('usersCard');
    if (usersCard) {
        usersCard.addEventListener('click', () => {
            switchView('users');
        });
    }

    // Admin card click
    const adminCard = document.getElementById('adminCard');
    if (adminCard) {
        adminCard.addEventListener('click', () => {
            switchView('admin');
        });
    }

    // Stores card click
    const storesCard = document.getElementById('storesCard');
    if (storesCard) {
        storesCard.addEventListener('click', () => {
            switchView('stores');
        });
    }

    // Search functionality
    const userSearch = document.getElementById('userSearch');
    const clearSearch = document.getElementById('clearSearch');

    if (userSearch) {
        userSearch.addEventListener('input', function () {
            toggleClearButton(this, clearSearch);
            filterUsers();
        });
    }

    if (clearSearch) {
        clearSearch.addEventListener('click', () => {
            userSearch.value = '';
            toggleClearButton(userSearch, clearSearch);
            filterUsers();
        });
    }

    // Admin search functionality
    const adminSearch = document.getElementById('adminSearch');
    const clearAdminSearch = document.getElementById('clearAdminSearch');

    if (adminSearch) {
        adminSearch.addEventListener('input', function () {
            toggleClearButton(this, clearAdminSearch);
            filterAdmins();
        });
    }

    if (clearAdminSearch) {
        clearAdminSearch.addEventListener('click', () => {
            adminSearch.value = '';
            toggleClearButton(adminSearch, clearAdminSearch);
            filterAdmins();
        });
    }

    // Store search functionality
    const storeSearch = document.getElementById('storeSearch');
    const clearStoreSearch = document.getElementById('clearStoreSearch');

    if (storeSearch) {
        storeSearch.addEventListener('input', function () {
            toggleClearButton(this, clearStoreSearch);
            filterStores();
        });
    }

    if (clearStoreSearch) {
        clearStoreSearch.addEventListener('click', () => {
            storeSearch.value = '';
            toggleClearButton(storeSearch, clearStoreSearch);
            filterStores();
        });
    }

    // Generate registration link
    const generateLinkBtn = document.getElementById('generateLinkBtn');
    if (generateLinkBtn) {
        generateLinkBtn.addEventListener('click', generateRegistrationLink);
    }

    // Copy registration link
    const copyLinkBtn = document.getElementById('copyLinkBtn');
    if (copyLinkBtn) {
        copyLinkBtn.addEventListener('click', copyRegistrationLink);
    }

    // NEW: Add event listener for the send announcement button
    const sendAnnouncementBtn = document.getElementById('sendAnnouncementBtn');
    if (sendAnnouncementBtn) {
        sendAnnouncementBtn.addEventListener('click', sendAnnouncement);
    }
}

// Helper function to toggle clear button visibility
function toggleClearButton(inputElement, clearButton) {
    if (inputElement.value.trim() !== '') {
        clearButton.classList.remove('hidden');
    } else {
        clearButton.classList.add('hidden');
    }
}

// Switch between views
function switchView(view) {
    currentView = view;

    // Update navigation active states
    navItems.forEach(item => {
        const itemText = item.querySelector('span').textContent.toLowerCase().replace(' ', '');
        if (itemText === view) {
            item.classList.add('text-primary-light', 'bg-green-50', 'font-medium');
            item.classList.remove('text-gray-600', 'hover:bg-gray-50');
        } else {
            item.classList.remove('text-primary-light', 'bg-green-50', 'font-medium');
            item.classList.add('text-gray-600', 'hover:bg-gray-50');
        }
    });

    // Update page title and content visibility
    if (pageTitle) {
        const titleText = pageTitle.querySelector('h2');
        if (titleText) {
            titleText.textContent = view.charAt(0).toUpperCase() + view.slice(1).replace(/([A-Z])/g, ' ').trim();
        }
    }

    // Toggle content visibility
    dashboardContent.classList.add('hidden');
    usersContent.classList.add('hidden');
    adminContent.classList.add('hidden');
    pendingRegistrationContent.classList.add('hidden');
    storesContent.classList.add('hidden');
    usersHistoryContent.classList.add('hidden');
    logsContent.classList.add('hidden');

    if (view === 'dashboard') {
        dashboardContent.classList.remove('hidden');
    } else if (view === 'users') {
        usersContent.classList.remove('hidden');
        loadUsersList();
    } else if (view === 'admin') {
        adminContent.classList.remove('hidden');
        loadAdminList();
    } else if (view === 'pendingregistration') {
        pendingRegistrationContent.classList.remove('hidden');
        loadPendingUsersList();
    } else if (view === 'stores') {
        storesContent.classList.remove('hidden');
        loadStoresList();
    } else if (view === 'usershistory') {
        usersHistoryContent.classList.remove('hidden');
        loadNavigationHistory();
    } else if (view === 'logs') {
        logsContent.classList.remove('hidden');
        loadLogs();
    }
}

// Load dashboard statistics
async function loadDashboardData() {
    try {
        console.log('Loading dashboard data...');
        const loadingIndicator = document.getElementById('loadingIndicator');

        // Get all users from Firestore
        const usersCollection = collection(db, 'users');
        const usersSnapshot = await getDocs(usersCollection);

        let userCount = 0;
        let adminCount = 0;
        allUsers = [];
        allAdmins = [];

        // Count users by role and store all users data
        usersSnapshot.forEach((doc) => {
            const userData = { id: doc.id, ...doc.data() };
            const role = userData.role;

            if (role === 'admin') {
                adminCount++;
                allAdmins.push(userData);
            } else {
                userCount++;
                allUsers.push(userData);
            }
        });

        // Get all stores from Firestore
        const storesCollection = collection(db, 'stores');
        const storesSnapshot = await getDocs(storesCollection);
        allStores = storesSnapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));

        console.log('Users count:', userCount);
        console.log('Admin count:', adminCount);
        console.log('Stores count:', allStores.length);

        // Update the UI
        document.getElementById('usersCount').textContent = userCount;
        document.getElementById('adminCount').textContent = adminCount;
        document.getElementById('storesCount').textContent = allStores.length;

        // Hide loading indicator
        if (loadingIndicator) {
            loadingIndicator.style.display = 'none';
        }

    } catch (error) {
        console.error('Error loading dashboard data:', error);
        const loadingIndicator = document.getElementById('loadingIndicator');
        if (loadingIndicator) {
            loadingIndicator.innerHTML = '<p class="text-red-500">Error loading dashboard data. Please refresh the page.</p>';
        }
    }
}

// NEW: Function to send an announcement
async function sendAnnouncement() {
    const title = document.getElementById('announcementTitle').value.trim();
    const message = document.getElementById('announcementMessage').value.trim();

    if (!title || !message) {
        alert('Please enter both a title and a message.');
        return;
    }

    try {
        const announcementsCollection = collection(db, 'announcements');
        await addDoc(announcementsCollection, {
            title: title,
            message: message,
            timestamp: Date.now()
        });

        await logAction('Sent announcement', { title });
        alert('Announcement sent successfully!');
        document.getElementById('announcementTitle').value = '';
        document.getElementById('announcementMessage').value = '';

    } catch (error) {
        console.error('Error sending announcement:', error);
        await logAction('Error sending announcement', { error: error.message });
        alert('Failed to send announcement. Please try again.');
    }
}


// Load users list for management
async function loadUsersList() {
    const usersTableBody = document.getElementById('usersTableBody');
    const usersLoading = document.getElementById('usersLoading');
    const noUsers = document.getElementById('noUsers');

    if (!usersTableBody || !usersLoading || !noUsers) return;

    usersLoading.classList.remove('hidden');
    usersTableBody.innerHTML = '';
    noUsers.classList.add('hidden');

    try {
        const sortedUsers = [...allUsers].sort((a, b) =>
            (a.username || '').localeCompare(b.username || '')
        );

        usersLoading.classList.add('hidden');

        if (sortedUsers.length === 0) {
            noUsers.classList.remove('hidden');
            return;
        }

        sortedUsers.forEach(user => {
            const userRow = createUserTableRow(user);
            usersTableBody.appendChild(userRow);
        });

    } catch (error) {
        console.error('Error loading users list:', error);
        usersLoading.innerHTML = '<p class="text-red-500 p-4">Error loading users. Please refresh the page.</p>';
    }
}

// Load admin list for management
async function loadAdminList() {
    const adminTableBody = document.getElementById('adminTableBody');
    const adminLoading = document.getElementById('adminLoading');
    const noAdmins = document.getElementById('noAdmins');

    if (!adminTableBody || !adminLoading || !noAdmins) return;

    adminLoading.classList.remove('hidden');
    adminTableBody.innerHTML = '';
    noAdmins.classList.add('hidden');

    try {
        const sortedAdmins = [...allAdmins].sort((a, b) =>
            (a.username || '').localeCompare(b.username || '')
        );

        adminLoading.classList.add('hidden');

        if (sortedAdmins.length === 0) {
            noAdmins.classList.remove('hidden');
            return;
        }

        sortedAdmins.forEach(admin => {
            const adminRow = createUserTableRow(admin);
            adminTableBody.appendChild(adminRow);
        });

    } catch (error) {
        console.error('Error loading admin list:', error);
        adminLoading.innerHTML = '<p class="text-red-500 p-4">Error loading admins. Please refresh the page.</p>';
    }
}

// Load stores list for management
async function loadStoresList() {
    const storesTableBody = document.getElementById('storesTableBody');
    const storesLoading = document.getElementById('storesLoading');
    const noStores = document.getElementById('noStores');

    if (!storesTableBody || !storesLoading || !noStores) return;

    storesLoading.classList.remove('hidden');
    storesTableBody.innerHTML = '';
    noStores.classList.add('hidden');

    try {
        const sortedStores = [...allStores].sort((a, b) =>
            (a.store_name || '').localeCompare(b.store_name || '')
        );

        storesLoading.classList.add('hidden');

        if (sortedStores.length === 0) {
            noStores.classList.remove('hidden');
            return;
        }

        sortedStores.forEach(store => {
            const storeRow = createStoreTableRow(store);
            storesTableBody.appendChild(storeRow);
        });

    } catch (error) {
        console.error('Error loading stores list:', error);
        storesLoading.innerHTML = '<p class="text-red-500 p-4">Error loading stores. Please refresh the page.</p>';
    }
}

// Load navigation history for management
async function loadNavigationHistory() {
    const navigationHistoryTableBody = document.getElementById('navigationHistoryTableBody');
    const navigationHistoryLoading = document.getElementById('navigationHistoryLoading');
    const noNavigationHistory = document.getElementById('noNavigationHistory');

    if (currentView === 'usershistory' && (!navigationHistoryTableBody || !navigationHistoryLoading || !noNavigationHistory)) return;

    if (currentView === 'usershistory') {
        navigationHistoryLoading.classList.remove('hidden');
        navigationHistoryTableBody.innerHTML = '';
        noNavigationHistory.classList.add('hidden');
    }

    try {
        // The Android app saves to a top-level collection "navigationHistory"
        const historyCollection = collection(db, 'navigationHistory');
        const q = query(historyCollection, orderBy('timestamp', 'desc'));
        const querySnapshot = await getDocs(q);

        if (querySnapshot.empty) {
            console.log('No navigation history found.');
            if (currentView === 'usershistory') {
                navigationHistoryLoading.classList.add('hidden');
                noNavigationHistory.classList.remove('hidden');
            }
            return;
        }

        // `allUsers` and `allAdmins` should be populated by `loadDashboardData` on page load.
        // We will map user IDs to emails for efficient lookup.
        const userEmailMap = [...allUsers, ...allAdmins].reduce((map, user) => {
            map[user.id] = user.email;
            return map;
        }, {});


        const navigationHistory = querySnapshot.docs.map(doc => {
            const data = doc.data();
            const userEmail = userEmailMap[data.user_id] || 'Unknown User'; // Lookup email

            return {
                id: doc.id,
                userEmail: userEmail,
                storeName: data.store_name || 'Unknown Store',
                timestamp: data.timestamp ? new Date(data.timestamp.toDate()) : null,
            };
        });

        allNavigationHistory = navigationHistory; // Update global variable

        if (currentView === 'usershistory') {
            navigationHistoryLoading.classList.add('hidden');

            if (allNavigationHistory.length === 0) {
                noNavigationHistory.classList.remove('hidden');
                return;
            }

            allNavigationHistory.forEach(entry => {
                const historyRow = createNavigationHistoryTableRow(entry);
                navigationHistoryTableBody.appendChild(historyRow);
            });
        }

        processNavigationForChart(allNavigationHistory);

    } catch (error) {
        console.error('Error loading navigation history:', error);
        if (currentView === 'usershistory') {
            navigationHistoryLoading.innerHTML = '<p class="text-red-500 p-4">Error loading navigation history. Please refresh the page.</p>';
        }
    }
}

function processNavigationForChart(history) {
    const oneWeekAgo = new Date();
    oneWeekAgo.setDate(oneWeekAgo.getDate() - 7);

    const recentHistory = history.filter(entry => entry.timestamp && entry.timestamp >= oneWeekAgo);

    const storeCounts = recentHistory.reduce((acc, entry) => {
        acc[entry.storeName] = (acc[entry.storeName] || 0) + 1;
        return acc;
    }, {});

    const sortedStores = Object.entries(storeCounts)
        .sort(([, a], [, b]) => b - a)
        .slice(0, 5);

    const labels = sortedStores.map(([name]) => name);
    const data = sortedStores.map(([, count]) => count);

    renderVisitedShopsChart(labels, data);
}

function renderVisitedShopsChart(labels, data) {
    const ctx = document.getElementById('visitedShopsChart').getContext('2d');
    if (visitedShopsChart) {
        visitedShopsChart.destroy();
    }
    visitedShopsChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Number of Visits',
                data: data,
                backgroundColor: 'rgba(22, 163, 74, 0.5)',
                borderColor: 'rgba(22, 163, 74, 1)',
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true
                }
            },
            responsive: true,
            maintainAspectRatio: false
        }
    });
}

// Create navigation history table row
function createNavigationHistoryTableRow(entry) {
    const row = document.createElement('tr');
    row.className = 'hover:bg-gray-50';

    const dateOptions = { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
    const formattedDate = entry.timestamp ? entry.timestamp.toLocaleDateString(undefined, dateOptions) : 'N/A';

    row.innerHTML = `
        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${entry.userEmail}</td>
        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${entry.storeName}</td>
        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${formattedDate}</td>
    `;

    return row;
}

// Create user table row
function createUserTableRow(user) {
    const row = document.createElement('tr');
    row.className = 'hover:bg-gray-50';

    const isDisabled = user.disabled || false;
    const statusText = isDisabled ? 'Suspended' : 'Active';
    const statusBadge = isDisabled
        ? `<span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-red-100 text-red-800">${statusText}</span>`
        : `<span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">${statusText}</span>`;

    const toggleActionText = isDisabled ? 'Activate' : 'Suspend';
    const toggleActionColor = isDisabled ? 'text-green-600 hover:text-green-900' : 'text-yellow-600 hover:text-yellow-900';

    row.innerHTML = `
        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${user.username || 'N/A'}</td>
        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${user.email || 'N/A'}</td>
        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${statusBadge}</td>
        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
            <button class="text-indigo-600 hover:text-indigo-900" data-action="edit" data-uid="${user.id}">Edit</button>
            <button class="${toggleActionColor} ml-4" data-action="toggle-status" data-uid="${user.id}">${toggleActionText}</button>
            <button class="text-red-600 hover:text-red-900 ml-4" data-action="delete" data-uid="${user.id}">Delete</button>
        </td>
    `;

    row.querySelector('button[data-action="delete"]').addEventListener('click', () => deleteUser(user.id, user.email));
    row.querySelector('button[data-action="edit"]').addEventListener('click', () => editUser(user.id));
    row.querySelector('button[data-action="toggle-status"]').addEventListener('click', () =>
    toggleUserStatus(user.id, user.email, isDisabled)
);


    return row;
}

// Create store table row
function createStoreTableRow(store) {
    const row = document.createElement('tr');
    row.className = 'hover:bg-gray-50';
    row.id = `store-row-${store.id}`;

    const status = store.status || 'pending';
    let statusBadge;
    let actionButtonsHTML = '';

    switch (status) {
        case 'approved':
            statusBadge = `<span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">Approved</span>`;
            actionButtonsHTML = `
                <button class="text-indigo-600 hover:text-indigo-900" data-action="edit" data-id="${store.id}">Edit</button>
                <button class="text-red-600 hover:text-red-900 ml-4" data-action="delete" data-id="${store.id}">Delete</button>
            `;
            break;
        case 'declined':
            statusBadge = `<span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-red-100 text-red-800">Declined</span>`;
            actionButtonsHTML = `
                <button class="text-green-600 hover:text-green-900" data-action="approve" data-id="${store.id}">Approve</button>
                <button class="text-red-600 hover:text-red-900 ml-4" data-action="delete" data-id="${store.id}">Delete</button>
            `;
            break;
        case 'pending':
        default:
            statusBadge = `<span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-yellow-100 text-yellow-800">Pending</span>`;
            actionButtonsHTML = `
                <button class="text-green-600 hover:text-green-900" data-action="approve" data-id="${store.id}">Approve</button>
                <button class="text-red-600 hover:text-red-900 ml-4" data-action="decline" data-id="${store.id}">Decline</button>
            `;
            break;
    }

    const businessPermitLink = store.business_permit_url
        ? `<a href="${store.business_permit_url}" target="_blank" class="text-indigo-600 hover:text-indigo-900">View Permit</a>`
        : 'N/A';

    row.innerHTML = `
        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${store.store_number || 'N/A'}</td>
        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${store.store_name || 'N/A'}</td>
        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${store.opening_time || 'N/A'}</td>
        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${store.closing_time || 'N/A'}</td>
        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${businessPermitLink}</td>
        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${statusBadge}</td>
        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
            ${actionButtonsHTML}
        </td>
    `;

    // Add event listeners
    const approveBtn = row.querySelector('button[data-action="approve"]');
    if (approveBtn) approveBtn.addEventListener('click', () => approveStore(store.id, store.store_name));

    const declineBtn = row.querySelector('button[data-action="decline"]');
    if (declineBtn) declineBtn.addEventListener('click', () => declineStore(store.id, store.store_name));

    const editBtn = row.querySelector('button[data-action="edit"]');
    if (editBtn) editBtn.addEventListener('click', () => editStore(store.id));

    const deleteBtn = row.querySelector('button[data-action="delete"]');
    if (deleteBtn) deleteBtn.addEventListener('click', () => deleteStore(store.id, store.store_name));

    return row;
}

// Approve a store
async function approveStore(id, name) {
    if (!confirm('Are you sure you want to approve this store?')) return;
    await updateStoreStatus(id, 'approved', name);
}

// Decline a store
async function declineStore(id, name) {
    if (!confirm('Are you sure you want to decline this store?')) return;
    await updateStoreStatus(id, 'declined', name);
}

// Generic function to update store status
async function updateStoreStatus(id, status, name) {
    try {
        const storeRef = doc(db, 'stores', id);
        await updateDoc(storeRef, { status: status });
        await logAction(`Store ${status}`, { storeId: id, storeName: name });

        // Refresh the list to show the updated status and actions
        await loadDashboardData(); // This re-fetches all data
        if (currentView === 'stores') {
            loadStoresList();
        }

        alert(`Store has been ${status}.`);

    } catch (error) {
        console.error(`Error updating store status to ${status}:`, error);
        await logAction(`Error updating store status to ${status}`, { storeId: id, storeName: name, error: error.message });
        alert('Failed to update store status. Please try again.');
    }
}

// Filter users based on search input
function filterUsers() {
    const searchTerm = document.getElementById('userSearch').value.toLowerCase().trim();
    const usersTableBody = document.getElementById('usersTableBody');
    const noUsers = document.getElementById('noUsers');

    if (!usersTableBody || !noUsers) return;

    const userRows = usersTableBody.querySelectorAll('tr');
    let visibleCount = 0;

    userRows.forEach(row => {
        const cells = row.querySelectorAll('td');
        const username = cells[0].textContent.toLowerCase();
        const email = cells[1].textContent.toLowerCase();

        if (username.includes(searchTerm) || email.includes(searchTerm)) {
            row.style.display = '';
            visibleCount++;
        } else {
            row.style.display = 'none';
        }
    });

    if (visibleCount === 0) {
        noUsers.classList.remove('hidden');
    } else {
        noUsers.classList.add('hidden');
    }
}

// Filter admins based on search input
function filterAdmins() {
    const searchTerm = document.getElementById('adminSearch').value.toLowerCase().trim();
    const adminTableBody = document.getElementById('adminTableBody');
    const noAdmins = document.getElementById('noAdmins');

    if (!adminTableBody || !noAdmins) return;

    const adminRows = adminTableBody.querySelectorAll('tr');
    let visibleCount = 0;

    adminRows.forEach(row => {
        const cells = row.querySelectorAll('td');
        const username = cells[0].textContent.toLowerCase();
        const email = cells[1].textContent.toLowerCase();

        if (username.includes(searchTerm) || email.includes(searchTerm)) {
            row.style.display = '';
            visibleCount++;
        } else {
            row.style.display = 'none';
        }
    });

    if (visibleCount === 0) {
        noAdmins.classList.remove('hidden');
    } else {
        noAdmins.classList.add('hidden');
    }
}

// Filter stores based on search input
function filterStores() {
    const searchTerm = document.getElementById('storeSearch').value.toLowerCase().trim();
    const storesTableBody = document.getElementById('storesTableBody');
    const noStores = document.getElementById('noStores');

    if (!storesTableBody || !noStores) return;

    const storeRows = storesTableBody.querySelectorAll('tr');
    let visibleCount = 0;

    storeRows.forEach(row => {
        const cells = row.querySelectorAll('td');
        const storeName = cells[0].textContent.toLowerCase();

        if (storeName.includes(searchTerm)) {
            row.style.display = '';
            visibleCount++;
        } else {
            row.style.display = 'none';
        }
    });

    if (visibleCount === 0) {
        noStores.classList.remove('hidden');
    } else {
        noStores.classList.add('hidden');
    }
}

// Generate and display registration link
async function generateRegistrationLink() {
    const generateLinkBtn = document.getElementById('generateLinkBtn');
    generateLinkBtn.disabled = true;
    generateLinkBtn.textContent = 'Generating...';

    try {
        // Generate a unique token
        const token = 'REG-' + Math.random().toString(36).substr(2, 10);

        // Store token in Firestore with an expiration date (e.g., 24 hours)
        const tokensCollection = collection(db, 'registrationTokens');
        await addDoc(tokensCollection, {
            token: token,
            expiresAt: Date.now() + 24 * 60 * 60 * 1000, // 24 hours
            createdAt: Date.now()
        });

        const registrationUrl = window.location.href.replace('dashboard.html', `signup.html?token=${token}`);

        // Display the link
        const registrationLinkInput = document.getElementById('registrationLink');
        registrationLinkInput.value = registrationUrl;
        document.getElementById('copyLinkBtn').classList.remove('hidden');
        await logAction('Generated registration link');

    } catch (error) {
        console.error('Error generating registration link:', error);
        await logAction('Error generating registration link', { error: error.message });
        alert('Failed to generate registration link. Please try again.');
    } finally {
        generateLinkBtn.disabled = false;
        generateLinkBtn.textContent = 'Generate';
    }
}

// Copy registration link to clipboard
function copyRegistrationLink() {
    const registrationLinkInput = document.getElementById('registrationLink');
    navigator.clipboard.writeText(registrationLinkInput.value).then(async () => {
        const copyBtn = document.getElementById('copyLinkBtn');
        copyBtn.textContent = 'Copied!';
        await logAction('Copied registration link');
        setTimeout(() => {
            copyBtn.textContent = 'Copy';
        }, 2000);
    }).catch(async (err) => {
        console.error('Failed to copy link:', err);
        await logAction('Error copying registration link', { error: err.message });
        alert('Failed to copy link. Please copy it manually.');
    });
}

// Load pending users for approval
async function loadPendingUsersList() {
    const tableBody = document.getElementById('pendingUsersTableBody');
    const loading = document.getElementById('pendingUsersLoading');
    const noUsers = document.getElementById('noPendingUsers');

    loading.classList.remove('hidden');
    tableBody.innerHTML = '';
    noUsers.classList.add('hidden');

    try {
        const pendingUsersCollection = collection(db, 'pendingUsers');
        const q = query(pendingUsersCollection, orderBy('createdAt', 'desc'));
        const querySnapshot = await getDocs(q);

        const pendingUsers = querySnapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));

        loading.classList.add('hidden');

        if (pendingUsers.length === 0) {
            noUsers.classList.remove('hidden');
            return;
        }

        pendingUsers.forEach(user => {
            const row = createPendingUserTableRow(user);
            tableBody.appendChild(row);
        });

    } catch (error) {
        console.error('Error loading pending users:', error);
        loading.innerHTML = '<p class="text-red-500 p-4">Error loading pending users. Please refresh.</p>';
    }
}

// Create table row for a pending user
function createPendingUserTableRow(user) {
    const row = document.createElement('tr');
    row.id = `pending-user-${user.id}`;
    row.className = 'hover:bg-gray-50';

    row.innerHTML = `
        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${user.username || 'N/A'}</td>
        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${user.email || 'N/A'}</td>
        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
            <button class="text-green-600 hover:text-green-900" data-action="approve" data-uid="${user.id}">Approve</button>
            <button class="text-red-600 hover:text-red-900 ml-4" data-action="reject" data-uid="${user.id}">Reject</button>
        </td>
    `;

    // Add event listeners for approve/reject buttons
    row.querySelector('[data-action="approve"]').addEventListener('click', () => approveUser(user.id, user.email));
    row.querySelector('[data-action="reject"]').addEventListener('click', () => rejectUser(user.id, user.email));

    return row;
}

// Approve a pending user
async function approveUser(uid, email) {
    if (!confirm('Are you sure you want to approve this user?')) return;

    try {
        // Get the pending user's data
        const pendingUserRef = doc(db, 'pendingUsers', uid);
        const pendingUserSnap = await getDoc(pendingUserRef);

        if (!pendingUserSnap.exists()) {
            throw new Error('Pending user not found.');
        }

        const pendingUserData = pendingUserSnap.data();

        // Create a new user in the 'users' collection
        const userRef = doc(db, 'users', uid);
        await setDoc(userRef, {
            email: pendingUserData.email,
            username: pendingUserData.username,
            createdAt: pendingUserData.createdAt,
            role: 'admin' // Assign 'admin' role upon approval
        });

        // Delete the user from the 'pendingUsers' collection
        await deleteDoc(pendingUserRef);

        await logAction('Approved user', { approvedUserId: uid, approvedUserEmail: email });

        // Refresh the list
        loadPendingUsersList();
        // Refresh dashboard data
        loadDashboardData();

        alert('User approved successfully.');

    } catch (error) {
        console.error('Error approving user:', error);
        await logAction('Error approving user', { approvedUserId: uid, approvedUserEmail: email, error: error.message });
        alert('Failed to approve user. Please try again.');
    }
}

// Reject a pending user
async function rejectUser(uid, email) {
    if (!confirm('Are you sure you want to reject this user? This action cannot be undone.')) return;

    try {
        // Delete from the 'pendingUsers' collection
        if (uid) {
            await deleteDoc(doc(db, 'pendingUsers', uid));
        }

        await logAction('Rejected user', { rejectedUserId: uid, rejectedUserEmail: email });

        // Refresh the list
        loadPendingUsersList();
        // Refresh dashboard data
        loadDashboardData();

        alert('User rejected successfully.');

    } catch (error) {
        console.error('Error rejecting user:', error);
        await logAction('Error rejecting user', { rejectedUserId: uid, rejectedUserEmail: email, error: error.message });
        alert('Failed to reject user. Please try again.');
    }
}

// NEW: Function to get the current user's ID token
async function getIdToken() {
    const user = auth.currentUser;
    if (user) {
        return await user.getIdToken(true); // Force refresh the token
    }
    return null;
}


async function deleteUserAuth(uid) {
    const idToken = await getIdToken();
    if (!idToken) {
        throw new Error('Authentication token not available.');
    }

    const response = await fetch(`http://localhost:3000/delete-user/${uid}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${idToken}`,
        },
    });

    if (!response.ok) {
        const errorData = await response.json().catch(() => ({ error: 'Failed to delete user and parse error response.' }));
        throw new Error(errorData.error || 'Failed to delete user from authentication provider.');
    }
}

// Delete a user
async function deleteUser(uid, email) {
    if (uid === auth.currentUser.uid) {
        alert("You cannot delete your own account.");
        return;
    }
    if (!confirm('Are you sure you want to delete this user? This action cannot be undone.')) return;

    try {
        await deleteUserAuth(uid);
        await deleteDoc(doc(db, 'users', uid));

        await logAction('Deleted user', { deletedUserId: uid, deletedUserEmail: email });

        // Refresh the list
        loadDashboardData();
        loadUsersList();

        alert('User deleted successfully.');

    } catch (error) {
        console.error('Error deleting user:', error);
        await logAction('Error deleting user', { deletedUserId: uid, deletedUserEmail: email, error: error.message });
        alert('Failed to delete user. Please try again.');
    }
}

// Delete a store
async function deleteStore(id, name) {
    if (!confirm('Are you sure you want to delete this store? This action cannot be undone.')) return;

    try {
        await deleteDoc(doc(db, 'stores', id));
        await logAction('Deleted store', { deletedStoreId: id, deletedStoreName: name });

        // Refresh the list
        loadDashboardData();
        loadStoresList();

        alert('Store deleted successfully.');

    } catch (error) {
        console.error('Error deleting store:', error);
        await logAction('Error deleting store', { deletedStoreId: id, deletedStoreName: name, error: error.message });
        alert('Failed to delete store. Please try again.');
    }
}

// Edit a store
function editStore(id) {
    const store = allStores.find(s => s.id === id);
    if (!store) {
        alert('Store not found!');
        return;
    }

    const modal = document.getElementById('editStoreModal');
    const storeIdInput = document.getElementById('editStoreId');
    const storeNameInput = document.getElementById('editStoreName');
    const openingTimeInput = document.getElementById('editOpeningTime');
    const closingTimeInput = document.getElementById('editClosingTime');

    storeIdInput.value = id;
    storeNameInput.value = store.store_name;
    openingTimeInput.value = store.opening_time;
    closingTimeInput.value = store.closing_time;

    modal.classList.remove('hidden');
}

// Edit a user
function editUser(id) {
    const user = allUsers.find(u => u.id === id) || allAdmins.find(u => u.id === id);
    if (!user) {
        alert('User not found!');
        return;
    }

    const modal = document.getElementById('editUserModal');
    const userIdInput = document.getElementById('editUserId');
    const usernameInput = document.getElementById('editUsername');

    userIdInput.value = id;
    usernameInput.value = user.username;

    modal.classList.remove('hidden');
}

// Save store changes
async function saveStoreChanges() {
    const id = document.getElementById('editStoreId').value;
    const storeName = document.getElementById('editStoreName').value;
    const openingTime = document.getElementById('editOpeningTime').value;
    const closingTime = document.getElementById('editClosingTime').value;

    if (!storeName || !openingTime || !closingTime) {
        alert('Please fill in all fields.');
        return;
    }

    try {
        const storeRef = doc(db, 'stores', id);
        await updateDoc(storeRef, {
            store_name: storeName,
            opening_time: openingTime,
            closing_time: closingTime,
        });

        await logAction('Updated store', { storeId: id, storeName: storeName });

        // Refresh the list
        await loadDashboardData();
        loadStoresList();

        // Hide the modal
        document.getElementById('editStoreModal').classList.add('hidden');

        alert('Store updated successfully.');

    } catch (error) {
        console.error('Error updating store:', error);
        await logAction('Error updating store', { storeId: id, storeName: storeName, error: error.message });
        alert('Failed to update store. Please try again.');
    }
}

// Save user changes
async function saveUserChanges() {
    const id = document.getElementById('editUserId').value;
    const username = document.getElementById('editUsername').value;

    if (!username) {
        alert('Please fill in all fields.');
        return;
    }

    try {
        const userRef = doc(db, 'users', id);
        await updateDoc(userRef, {
            username: username,
        });

        await logAction('Updated user', { userId: id, username: username });

        // Refresh the list
        await loadDashboardData();
        if(currentView === 'users'){
            loadUsersList();
        } else if(currentView === 'admin'){
            loadAdminList();
        }

        // Hide the modal
        document.getElementById('editUserModal').classList.add('hidden');

        alert('User updated successfully.');

    } catch (error) {
        console.error('Error updating user:', error);
        await logAction('Error updating user', { userId: id, username: username, error: error.message });
        alert('Failed to update user. Please try again.');
    }
}

// Close edit modal
function cancelEdit() {
    document.getElementById('editStoreModal').classList.add('hidden');
}

function cancelUserEdit() {
    document.getElementById('editUserModal').classList.add('hidden');
}


async function updateUserAuthStatus(uid, isDisabled) {
    const idToken = await getIdToken();
    if (!idToken) {
        throw new Error('Authentication token not available.');
    }

    const response = await fetch(`http://localhost:3000/update-user-status/${uid}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${idToken}`,
        },
        body: JSON.stringify({ disabled: isDisabled }),
    });

    if (!response.ok) {
        const errorData = await response.json().catch(() => ({ error: 'Failed to update status and parse error response.' }));
        throw new Error(errorData.error || 'Failed to update user status in authentication provider.');
    }
}

// Toggle user status (suspend/activate)
async function toggleUserStatus(uid, email, newStatus) {
    if (!confirm(`Are you sure you want to ${newStatus ? 'activate' : 'suspend'} this user?`)) return;

    try {
        // Update Firestore only
        await updateDoc(doc(db, 'users', uid), {
            disabled: !newStatus,
            updatedAt: Timestamp.now()
        });

        // Log action
        await logAction(`${newStatus ? 'Activated' : 'Suspended'} user`, {
            uid: uid,
            email: email
        });

        alert(`User ${newStatus ? 'activated' : 'suspended'} successfully.`);

        // Refresh table
        await loadDashboardData();
        await loadUsersList();

    } catch (error) {
        console.error('Error toggling user status:', error);
        alert('Failed to update user status.');
    }
}



// Logout functionality
const logoutBtn = document.getElementById('logoutBtn');
if (logoutBtn) {
    logoutBtn.addEventListener('click', async () => {
        try {
            await logAction('Logged out');
            console.log('Logging out...');
            await signOut(auth);

            // Clear localStorage
            localStorage.removeItem('loggedInRole');
            localStorage.removeItem('userId');

            console.log('Logout successful, redirecting to login...');
            window.location.href = 'index.html';
        } catch (error) {
            console.error('Logout error:', error);
            await logAction('Logout error', { error: error.message });
            alert('Error logging out. Please try again.');
        }
    });
}

// Event Listeners for Modal
const saveStoreBtn = document.getElementById('saveStoreBtn');
if (saveStoreBtn) {
    saveStoreBtn.addEventListener('click', saveStoreChanges);
}

const cancelEditBtn = document.getElementById('cancelEditBtn');
if (cancelEditBtn) {
    cancelEditBtn.addEventListener('click', cancelEdit);
}

const saveUserBtn = document.getElementById('saveUserBtn');
if (saveUserBtn) {
    saveUserBtn.addEventListener('click', saveUserChanges);
}

const cancelUserEditBtn = document.getElementById('cancelUserEditBtn');
if (cancelUserEditBtn) {
    cancelUserEditBtn.addEventListener('click', cancelUserEdit);
}

// Mobile menu toggle functionality
const mobileMenuButton = document.getElementById('mobileMenuButton');
const sidebar = document.getElementById('sidebar');
const sidebarBackdrop = document.getElementById('sidebarBackdrop');

if (mobileMenuButton && sidebar && sidebarBackdrop) {
    mobileMenuButton.addEventListener('click', () => {
        sidebar.classList.toggle('-translate-x-full');
        sidebarBackdrop.classList.toggle('hidden');
    });

    sidebarBackdrop.addEventListener('click', () => {
        sidebar.classList.add('-translate-x-full');
        sidebarBackdrop.classList.add('hidden');
    });
}

// LOGGING FUNCTIONS

async function logAction(action, details = {}) {
    try {
        const user = auth.currentUser;
        const logData = {
            timestamp: Timestamp.now(),
            userId: user ? user.uid : 'System',
            userEmail: user ? user.email : 'System',
            action: action,
            details: details
        };
        await addDoc(collection(db, 'logs'), logData);
    } catch (error) {
        console.error('Error writing to log:', error);
    }
}

async function loadLogs() {
    const logsTableBody = document.getElementById('logsTableBody');
    const logsLoading = document.getElementById('logsLoading');
    const noLogs = document.getElementById('noLogs');

    if (!logsTableBody || !logsLoading || !noLogs) return;

    logsLoading.classList.remove('hidden');
    logsTableBody.innerHTML = '';
    noLogs.classList.add('hidden');

    try {
        const logsCollection = collection(db, 'logs');
        const q = query(logsCollection, orderBy('timestamp', 'desc'));
        const querySnapshot = await getDocs(q);

        logsLoading.classList.add('hidden');

        if (querySnapshot.empty) {
            noLogs.classList.remove('hidden');
            return;
        }

        querySnapshot.forEach(doc => {
            const logEntry = doc.data();
            const logRow = createLogTableRow(logEntry);
            logsTableBody.appendChild(logRow);
        });

    } catch (error) {
        console.error('Error loading logs:', error);
        logsLoading.innerHTML = '<p class="text-red-500 p-4">Error loading logs. Please refresh the page.</p>';
    }
}

function createLogTableRow(entry) {
    const row = document.createElement('tr');
    row.className = 'hover:bg-gray-50';

    const dateOptions = { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit', second: '2-digit' };
    const formattedDate = entry.timestamp ? entry.timestamp.toDate().toLocaleDateString(undefined, dateOptions) : 'N/A';

    row.innerHTML = `
        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${formattedDate}</td>
        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${entry.userEmail}</td>
        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${entry.action}</td>
        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${JSON.stringify(entry.details)}</td>
    `;

    return row;
}

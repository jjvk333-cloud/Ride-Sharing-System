/**
 * VELTO Main Application Controller
 * Manages view switching, interactive modals, dynamic rendering, and pattern demonstrations.
 */

let allRides = [];
let currentCalcRide = null;

// Initialize on document ready
document.addEventListener('DOMContentLoaded', () => {
  checkBackendHealth();
  updateAuthUI();
  loadRides();

  // Set default date for ride creation to tomorrow
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  const dateInput = document.getElementById('cr-date');
  if (dateInput) {
    dateInput.value = tomorrow.toISOString().split('T')[0];
  }

  // Auto-refresh notifications every 15s if logged in
  setInterval(() => {
    if (Auth.isLoggedIn()) {
      pollNotifications();
    }
  }, 15000);
});

// Toast notification helper
function showToast(message, type = 'primary') {
  const toastEl = document.getElementById('appToast');
  const toastBody = document.getElementById('toastMessage');
  toastEl.className = `toast align-items-center text-white bg-${type} border-0`;
  toastBody.textContent = message;
  const toast = new bootstrap.Toast(toastEl, { delay: 4000 });
  toast.show();
}

// Check backend connectivity
async function checkBackendHealth() {
  const badge = document.getElementById('backend-status-badge');
  try {
    const res = await API.health();
    if (res && res.status === 'UP') {
      badge.innerHTML = `<span class="badge bg-success"><i class="bi bi-check-circle-fill"></i> Backend Online</span>`;
    } else {
      badge.innerHTML = `<span class="badge bg-warning text-dark"><i class="bi bi-exclamation-triangle"></i> Status Unknown</span>`;
    }
  } catch (err) {
    badge.innerHTML = `<span class="badge bg-danger"><i class="bi bi-x-circle-fill"></i> Backend Offline (Port 8080)</span>`;
  }
}

// Navigation / View Switching
function showView(viewName) {
  document.querySelectorAll('.app-view').forEach(v => v.classList.add('d-none'));
  document.querySelectorAll('.navbar-nav .nav-link').forEach(l => l.classList.remove('active'));

  const targetView = document.getElementById(`view-${viewName}`);
  const targetNav = document.getElementById(`nav-${viewName}`);

  if (targetView) targetView.classList.remove('d-none');
  if (targetNav) targetNav.classList.add('active');

  // Trigger loads based on view
  if (viewName === 'search') loadRides();
  if (viewName === 'passenger') loadPassengerBookings();
  if (viewName === 'driver') loadDriverRides();
  if (viewName === 'admin') loadAdminDashboard();
}

// Update UI based on logged-in state and roles
function updateAuthUI() {
  const user = Auth.getUser();
  const loggedOut = document.getElementById('auth-logged-out');
  const loggedIn = document.getElementById('auth-logged-in');
  const notifWrapper = document.getElementById('notif-wrapper');
  const nameSpan = document.getElementById('user-display-name');

  // Hide all role-specific nav items first
  document.querySelectorAll('.auth-role').forEach(el => el.classList.add('d-none'));

  if (user) {
    loggedOut.classList.add('d-none');
    loggedIn.classList.remove('d-none');
    notifWrapper.classList.remove('d-none');
    nameSpan.textContent = `${user.name} (${user.role})`;

    // Show appropriate role menus
    if (user.role === 'PASSENGER') {
      document.querySelector('.auth-passenger')?.classList.remove('d-none');
    } else if (user.role === 'DRIVER') {
      document.querySelector('.auth-driver')?.classList.remove('d-none');
      document.querySelector('.auth-passenger')?.classList.remove('d-none');
    } else if (user.role === 'ADMIN') {
      document.querySelector('.auth-admin')?.classList.remove('d-none');
      document.querySelector('.auth-passenger')?.classList.remove('d-none');
      document.querySelector('.auth-driver')?.classList.remove('d-none');
    }

    pollNotifications();
  } else {
    loggedOut.classList.remove('d-none');
    loggedIn.classList.add('d-none');
    notifWrapper.classList.add('d-none');
  }
}

// Quick 1-Click Demo Login
async function quickLogin(email, password) {
  try {
    const user = await API.auth.login(email, password);
    Auth.setUser(user);
    updateAuthUI();
    showToast(`Logged in as ${user.name} (${user.role})!`, 'success');

    // Auto-navigate to role dashboard
    if (user.role === 'ADMIN') showView('admin');
    else if (user.role === 'DRIVER') showView('driver');
    else showView('passenger');
  } catch (err) {
    showToast(`Login failed: ${err.message}`, 'danger');
  }
}

// Manual Login Form
async function handleLoginForm(e) {
  e.preventDefault();
  const email = document.getElementById('login-email').value;
  const password = document.getElementById('login-password').value;

  try {
    const user = await API.auth.login(email, password);
    Auth.setUser(user);
    updateAuthUI();
    bootstrap.Modal.getInstance(document.getElementById('authModal')).hide();
    showToast(`Welcome back, ${user.name}!`, 'success');

    if (user.role === 'ADMIN') showView('admin');
    else if (user.role === 'DRIVER') showView('driver');
    else showView('passenger');
  } catch (err) {
    showToast(`Login failed: ${err.message}`, 'danger');
  }
}

// Manual Registration Form (Factory Method Pattern)
async function handleRegisterForm(e) {
  e.preventDefault();
  const req = {
    name: document.getElementById('reg-name').value,
    email: document.getElementById('reg-email').value,
    password: document.getElementById('reg-password').value,
    phone: document.getElementById('reg-phone').value,
    role: document.getElementById('reg-role').value
  };

  if (req.role === 'DRIVER') {
    req.vehicleNumber = document.getElementById('reg-vehicleNumber').value;
    req.licenseNumber = document.getElementById('reg-licenseNumber').value;
  }

  try {
    const user = await API.auth.register(req);
    Auth.setUser(user);
    updateAuthUI();
    bootstrap.Modal.getInstance(document.getElementById('authModal')).hide();
    showToast(`Account created via UserFactory! Welcome, ${user.name}`, 'success');
  } catch (err) {
    showToast(`Registration failed: ${err.message}`, 'danger');
  }
}

function handleLogout() {
  Auth.logout();
  updateAuthUI();
  showView('search');
  showToast('Logged out successfully.', 'info');
}

function setAuthTab(tab) {
  const loginTab = document.getElementById('tab-login');
  const regTab = document.getElementById('tab-register');
  const formLogin = document.getElementById('form-login');
  const formRegister = document.getElementById('form-register');

  if (tab === 'login') {
    loginTab.classList.add('active');
    regTab.classList.remove('active');
    formLogin.classList.remove('d-none');
    formRegister.classList.add('d-none');
  } else {
    regTab.classList.add('active');
    loginTab.classList.remove('active');
    formRegister.classList.remove('d-none');
    formLogin.classList.add('d-none');
  }
}

function toggleDriverFields() {
  const role = document.getElementById('reg-role').value;
  const fields = document.getElementById('driver-reg-fields');
  if (role === 'DRIVER') fields.classList.remove('d-none');
  else fields.classList.add('d-none');
}

// ==========================================
// RIDES & PRICING (Builder & Strategy Pattern)
// ==========================================
async function loadRides(pickup = '', destination = '') {
  const container = document.getElementById('rides-catalogue-container');
  container.innerHTML = `<div class="col-12 text-center py-4"><div class="spinner-border text-primary" role="status"></div></div>`;

  try {
    const rides = await API.rides.getAll(pickup, destination);
    allRides = rides;

    if (!rides || rides.length === 0) {
      container.innerHTML = `
        <div class="col-12 text-center py-5">
          <i class="bi bi-geo-alt fs-1 text-muted"></i>
          <h5 class="text-muted mt-2">No rides available matching your search criteria.</h5>
          <button class="btn btn-outline-primary btn-sm mt-2" onclick="loadRides()">Show All Rides</button>
        </div>`;
      return;
    }

    container.innerHTML = rides.map(ride => `
      <div class="col-md-6 col-lg-4">
        <div class="ride-card p-3 h-100 d-flex flex-column">
          <div class="d-flex justify-content-between align-items-start mb-2">
            <span class="badge badge-status-${ride.status} px-2 py-1">${ride.status}</span>
            <span class="price-tag">₹${ride.price.toFixed(2)}</span>
          </div>

          <h5 class="fw-bold mb-1 text-dark">${ride.pickup}</h5>
          <div class="text-muted small mb-2"><i class="bi bi-arrow-down"></i> to</div>
          <h5 class="fw-bold mb-3 text-primary">${ride.destination}</h5>

          <div class="border-top pt-2 mt-auto small text-muted">
            <div class="d-flex justify-content-between mb-1">
              <span><i class="bi bi-calendar-event me-1"></i> ${ride.date}</span>
              <span><i class="bi bi-clock me-1"></i> ${ride.time}</span>
            </div>
            <div class="d-flex justify-content-between mb-3">
              <span><i class="bi bi-person-fill me-1"></i> Driver: ${ride.driverName}</span>
              <span class="badge bg-light text-dark border">
                <i class="bi bi-people-fill text-primary"></i> ${ride.availableSeats} seats left
              </span>
            </div>
            <div class="d-flex gap-2">
              <button class="btn btn-outline-primary btn-sm flex-grow-1" onclick="openPriceModal('${ride.id}')">
                <i class="bi bi-calculator me-1"></i> Preview Pricing
              </button>
              <button class="btn btn-success btn-sm fw-bold px-3" onclick="bookRideDirect('${ride.id}')">
                Book
              </button>
            </div>
          </div>
        </div>
      </div>
    `).join('');
  } catch (err) {
    container.innerHTML = `<div class="col-12 alert alert-danger">Failed to load rides: ${err.message}</div>`;
  }
}

function handleSearch(e) {
  e.preventDefault();
  const pickup = document.getElementById('search-pickup').value.trim();
  const dest = document.getElementById('search-destination').value.trim();
  loadRides(pickup, dest);
}

// Strategy Pattern: Open Calculator Modal
function openPriceModal(rideId) {
  const ride = allRides.find(r => r.id === rideId);
  if (!ride) return;
  currentCalcRide = ride;

  document.getElementById('calc-ride-id').value = ride.id;
  document.getElementById('calc-seats').value = 1;
  document.getElementById('calc-seats').max = ride.availableSeats || 4;
  document.getElementById('strat-std').checked = true;

  recalculatePrice();
  new bootstrap.Modal(document.getElementById('priceModal')).show();
}

async function recalculatePrice() {
  if (!currentCalcRide) return;
  const seats = parseInt(document.getElementById('calc-seats').value, 10) || 1;
  const strategy = document.querySelector('input[name="pricingOption"]:checked')?.value || 'STANDARD';

  try {
    const calc = await API.rides.calculatePrice(currentCalcRide.id, seats, strategy);
    document.getElementById('calc-strat-name').textContent = calc.strategyName;
    document.getElementById('calc-total-fare').textContent = `₹${calc.totalPrice.toFixed(2)}`;
  } catch (err) {
    console.error('Price calculation error:', err);
  }
}

async function proceedToBookFromCalc() {
  if (!currentCalcRide) return;
  const seats = parseInt(document.getElementById('calc-seats').value, 10) || 1;
  const strategy = document.querySelector('input[name="pricingOption"]:checked')?.value || 'STANDARD';

  bootstrap.Modal.getInstance(document.getElementById('priceModal')).hide();
  executeBooking(currentCalcRide.id, seats, strategy);
}

function bookRideDirect(rideId) {
  executeBooking(rideId, 1, 'STANDARD');
}

// ==========================================
// BOOKING (Facade Pattern)
// ==========================================
async function executeBooking(rideId, seats, pricingType) {
  if (!Auth.isLoggedIn()) {
    showToast('Please sign in or use 1-Click Demo Login to book a ride!', 'warning');
    new bootstrap.Modal(document.getElementById('authModal')).show();
    return;
  }

  const user = Auth.getUser();
  const req = {
    rideId: rideId,
    passengerId: user.id,
    seats: seats,
    pricingType: pricingType
  };

  try {
    const res = await API.bookings.create(req);
    showToast(`Ride Booked! Booking ID: ${res.id}`, 'success');
    loadRides(); // refresh available seats
    showView('passenger');
  } catch (err) {
    showToast(`Booking failed: ${err.message}`, 'danger');
  }
}

// Passenger Portal
async function loadPassengerBookings() {
  if (!Auth.isLoggedIn()) return;
  const user = Auth.getUser();
  const tbody = document.getElementById('passenger-bookings-body');
  tbody.innerHTML = `<tr><td colspan="8" class="text-center py-3"><div class="spinner-border spinner-border-sm text-primary"></div></td></tr>`;

  try {
    const bookings = await API.bookings.getByUser(user.id);
    if (!bookings || bookings.length === 0) {
      tbody.innerHTML = `<tr><td colspan="8" class="text-center text-muted py-4">No bookings found. Book a ride from the catalogue!</td></tr>`;
      return;
    }

    tbody.innerHTML = bookings.map(b => `
      <tr>
        <td><code>${b.id.substring(0, 8)}...</code></td>
        <td><strong>${b.rideId}</strong></td>
        <td><small>${new Date(b.createdAt).toLocaleDateString()}</small></td>
        <td><span class="badge bg-light text-dark">${b.seats} seat(s)</span></td>
        <td><strong>₹${b.amount.toFixed(2)}</strong></td>
        <td><span class="badge badge-status-${b.bookingStatus}">${b.bookingStatus}</span></td>
        <td><span class="badge badge-${b.paymentStatus.toLowerCase()}">${b.paymentStatus}</span></td>
        <td>
          <div class="btn-group btn-group-sm">
            ${b.paymentStatus === 'PENDING' ? `
              <button class="btn btn-outline-success" onclick="openPaymentModal('${b.id}', ${b.amount})">
                Pay Now
              </button>` : ''}
            ${b.bookingStatus !== 'CANCELLED' ? `
              <button class="btn btn-outline-danger" onclick="cancelBooking('${b.id}')">
                Cancel
              </button>` : `<span class="text-muted small">None</span>`}
          </div>
        </td>
      </tr>
    `).join('');
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="8" class="text-danger py-3">Error: ${err.message}</td></tr>`;
  }
}

async function cancelBooking(bookingId) {
  if (!confirm('Are you sure you want to cancel this booking? Seats will be restored to the ride.')) return;
  try {
    await API.bookings.cancel(bookingId);
    showToast('Booking cancelled and seats restored to ride!', 'info');
    loadPassengerBookings();
    loadRides();
  } catch (err) {
    showToast(`Cancellation failed: ${err.message}`, 'danger');
  }
}

// ==========================================
// PAYMENTS (Adapter Pattern)
// ==========================================
function openPaymentModal(bookingId, amount) {
  document.getElementById('pay-booking-id').value = bookingId;
  document.getElementById('pay-amount').value = amount;
  document.getElementById('pay-display-booking-id').textContent = bookingId.substring(0, 8) + '...';
  document.getElementById('pay-display-amount').textContent = `₹${amount.toFixed(2)}`;

  handlePaymentMethodChange();
  new bootstrap.Modal(document.getElementById('paymentModal')).show();
}

function handlePaymentMethodChange() {
  const method = document.getElementById('pay-method').value;
  document.getElementById('pay-upi-fields').classList.add('d-none');
  document.getElementById('pay-card-fields').classList.add('d-none');
  document.getElementById('pay-mock-fields').classList.add('d-none');

  if (method === 'UPI') document.getElementById('pay-upi-fields').classList.remove('d-none');
  else if (method === 'CARD') document.getElementById('pay-card-fields').classList.remove('d-none');
  else document.getElementById('pay-mock-fields').classList.remove('d-none');
}

async function submitPayment() {
  const user = Auth.getUser();
  const bookingId = document.getElementById('pay-booking-id').value;
  const amount = parseFloat(document.getElementById('pay-amount').value);
  const method = document.getElementById('pay-method').value;

  const req = {
    bookingId: bookingId,
    passengerId: user.id,
    amount: amount,
    paymentMethod: method
  };

  if (method === 'UPI') {
    req.upiId = document.getElementById('pay-upiId').value;
  } else if (method === 'CARD') {
    req.cardNumber = document.getElementById('pay-cardNum').value;
    req.expiryDate = document.getElementById('pay-exp').value;
    req.cvv = document.getElementById('pay-cvv').value;
  }

  try {
    const payment = await API.payments.process(req);
    bootstrap.Modal.getInstance(document.getElementById('paymentModal')).hide();

    if (payment.paymentStatus === 'PAID') {
      showToast(`Payment Successful via ${method} Adapter! Ref: ${payment.gatewayReference}`, 'success');
    } else {
      showToast(`Payment Declined: ${payment.message}`, 'danger');
    }
    loadPassengerBookings();
  } catch (err) {
    showToast(`Payment processing error: ${err.message}`, 'danger');
  }
}

// ==========================================
// DRIVER & STATE PATTERN
// ==========================================
async function loadDriverRides() {
  if (!Auth.isLoggedIn()) return;
  const user = Auth.getUser();
  const tbody = document.getElementById('driver-rides-body');
  tbody.innerHTML = `<tr><td colspan="6" class="text-center py-3"><div class="spinner-border spinner-border-sm text-info"></div></td></tr>`;

  try {
    const rides = await API.rides.getByDriver(user.id);
    if (!rides || rides.length === 0) {
      tbody.innerHTML = `<tr><td colspan="6" class="text-center text-muted py-4">No rides published yet. Click "Offer New Ride" to create one!</td></tr>`;
      return;
    }

    tbody.innerHTML = rides.map(r => `
      <tr>
        <td>
          <strong>${r.pickup}</strong> &rarr; <strong>${r.destination}</strong><br>
          <small class="text-muted">ID: ${r.id.substring(0, 8)}...</small>
        </td>
        <td>${r.date} at ${r.time}</td>
        <td><span class="badge bg-light text-dark">${r.availableSeats}</span></td>
        <td><strong>₹${r.price.toFixed(2)}</strong></td>
        <td><span class="badge badge-status-${r.status}">${r.status}</span></td>
        <td>
          <div class="btn-group btn-group-sm">
            ${r.status === 'REQUESTED' ? `
              <button class="btn btn-outline-primary" onclick="advanceRideStatus('${r.id}', 'CONFIRMED')">Confirm</button>` : ''}
            ${r.status === 'CONFIRMED' ? `
              <button class="btn btn-outline-purple" style="border-color:#7c3aed;color:#7c3aed;" onclick="advanceRideStatus('${r.id}', 'DRIVER_ASSIGNED')">Assign Self</button>` : ''}
            ${r.status === 'DRIVER_ASSIGNED' ? `
              <button class="btn btn-outline-warning" onclick="advanceRideStatus('${r.id}', 'DRIVER_ARRIVING')">Arriving</button>` : ''}
            ${r.status === 'DRIVER_ARRIVING' ? `
              <button class="btn btn-outline-primary" onclick="advanceRideStatus('${r.id}', 'IN_PROGRESS')">Start Ride</button>` : ''}
            ${r.status === 'IN_PROGRESS' ? `
              <button class="btn btn-outline-success" onclick="advanceRideStatus('${r.id}', 'COMPLETED')">Complete Ride</button>` : ''}
            ${(r.status !== 'COMPLETED' && r.status !== 'CANCELLED') ? `
              <button class="btn btn-outline-danger" onclick="advanceRideStatus('${r.id}', 'CANCELLED')">Cancel</button>` : `
              <span class="text-muted small">Terminal State</span>`}
          </div>
        </td>
      </tr>
    `).join('');
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="6" class="text-danger py-3">Error: ${err.message}</td></tr>`;
  }
}

async function advanceRideStatus(rideId, nextStatus) {
  try {
    const updated = await API.rides.updateStatus(rideId, nextStatus);
    showToast(`Ride State advanced to ${updated.status} (State Pattern)`, 'success');
    loadDriverRides();
    loadRides();
    pollNotifications();
  } catch (err) {
    showToast(`Invalid State Transition: ${err.message}`, 'danger');
  }
}

async function handleCreateRide(e) {
  e.preventDefault();
  const user = Auth.getUser();
  const req = {
    driverId: user.id,
    driverName: user.name,
    pickup: document.getElementById('cr-pickup').value,
    destination: document.getElementById('cr-destination').value,
    date: document.getElementById('cr-date').value,
    time: document.getElementById('cr-time').value,
    availableSeats: parseInt(document.getElementById('cr-seats').value, 10),
    price: parseFloat(document.getElementById('cr-price').value)
  };

  try {
    await API.rides.create(req);
    bootstrap.Modal.getInstance(document.getElementById('createRideModal')).hide();
    document.getElementById('create-ride-form').reset();
    showToast('New Ride constructed and published (Builder Pattern)!', 'success');
    loadDriverRides();
    loadRides();
  } catch (err) {
    showToast(`Failed to create ride: ${err.message}`, 'danger');
  }
}

// ==========================================
// NOTIFICATIONS (Observer Pattern)
// ==========================================
async function pollNotifications() {
  if (!Auth.isLoggedIn()) return;
  const user = Auth.getUser();

  try {
    const notifs = await API.notifications.getByUser(user.id);
    const unread = notifs.filter(n => !n.read);
    const badge = document.getElementById('notif-badge');

    if (unread.length > 0) {
      badge.textContent = unread.length;
      badge.classList.remove('d-none');
    } else {
      badge.classList.add('d-none');
    }

    renderNotifications(notifs);
  } catch (err) {
    console.error('Error polling notifications:', err);
  }
}

function renderNotifications(notifs) {
  const container = document.getElementById('notif-list-container');
  if (!notifs || notifs.length === 0) {
    container.innerHTML = `<div class="p-4 text-center text-muted">No notifications yet.</div>`;
    return;
  }

  container.innerHTML = notifs.map(n => `
    <div class="notification-item p-3 border-bottom ${n.read ? '' : 'unread'}">
      <div class="d-flex justify-content-between align-items-start">
        <span class="badge bg-secondary mb-1">${n.type}</span>
        <small class="text-muted">${new Date(n.createdAt).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}</small>
      </div>
      <p class="mb-1 small">${n.message}</p>
      ${!n.read ? `
        <button class="btn btn-link btn-sm p-0 text-primary" onclick="markNotificationRead('${n.id}')">
          Mark as read
        </button>` : ''}
    </div>
  `).join('');
}

async function markNotificationRead(notifId) {
  try {
    await API.notifications.markAsRead(notifId);
    pollNotifications();
  } catch (err) {
    console.error(err);
  }
}

function toggleNotifPanel() {
  const offcanvas = new bootstrap.Offcanvas(document.getElementById('notifOffcanvas'));
  offcanvas.show();
}

// ==========================================
// ADMIN DASHBOARD (Singleton Pattern)
// ==========================================
async function loadAdminDashboard() {
  try {
    const [users, rides, bookings, cfg] = await Promise.all([
      API.users.getAll(),
      API.rides.getAll(),
      API.bookings.getAll(),
      API.config.get()
    ]);

    // Metrics
    document.getElementById('metric-users').textContent = users.length;
    document.getElementById('metric-rides').textContent = rides.length;
    document.getElementById('metric-bookings').textContent = bookings.length;

    // Config form
    document.getElementById('cfg-baseFare').value = cfg.baseFare;
    document.getElementById('cfg-perKmRate').value = cfg.perKmRate;
    document.getElementById('cfg-surgeMultiplier').value = cfg.surgeMultiplier;
    document.getElementById('cfg-sharedDiscount').value = cfg.sharedDiscountMultiplier;
    document.getElementById('cfg-platformFee').value = cfg.platformFeePercentage;
    document.getElementById('cfg-currency').value = cfg.currency;
    document.getElementById('cfg-maintenanceMode').value = cfg.maintenanceMode.toString();

    // User Directory
    const usersBody = document.getElementById('admin-users-body');
    usersBody.innerHTML = users.map(u => `
      <tr>
        <td><strong>${u.name}</strong></td>
        <td>${u.email}</td>
        <td><span class="badge ${u.role === 'ADMIN' ? 'bg-danger' : (u.role === 'DRIVER' ? 'bg-info' : 'bg-success')}">${u.role}</span></td>
        <td>${u.phone || 'N/A'}</td>
        <td>${u.vehicleNumber ? `${u.vehicleNumber} (${u.licenseNumber || 'Verified'})` : '-'}</td>
      </tr>
    `).join('');
  } catch (err) {
    showToast(`Admin load error: ${err.message}`, 'danger');
  }
}

async function handleUpdateConfig(e) {
  e.preventDefault();
  const req = {
    baseFare: parseFloat(document.getElementById('cfg-baseFare').value),
    perKmRate: parseFloat(document.getElementById('cfg-perKmRate').value),
    surgeMultiplier: parseFloat(document.getElementById('cfg-surgeMultiplier').value),
    sharedDiscountMultiplier: parseFloat(document.getElementById('cfg-sharedDiscount').value),
    platformFeePercentage: parseFloat(document.getElementById('cfg-platformFee').value),
    currency: document.getElementById('cfg-currency').value,
    maintenanceMode: document.getElementById('cfg-maintenanceMode').value === 'true'
  };

  try {
    await API.config.update(req);
    showToast('AppConfigSingleton updated dynamically at runtime!', 'success');
  } catch (err) {
    showToast(`Config update failed: ${err.message}`, 'danger');
  }
}

async function resetConfig() {
  try {
    await API.config.reset();
    showToast('AppConfigSingleton reset to factory defaults.', 'info');
    loadAdminDashboard();
  } catch (err) {
    showToast(`Reset error: ${err.message}`, 'danger');
  }
}

/**
 * VELTO Client-side Auth & Session Manager
 */
const Auth = {
  KEY_USER: 'velto_user',

  getUser() {
    try {
      const data = localStorage.getItem(this.KEY_USER);
      return data ? JSON.parse(data) : null;
    } catch (e) {
      return null;
    }
  },

  setUser(user) {
    localStorage.setItem(this.KEY_USER, JSON.stringify(user));
  },

  logout() {
    localStorage.removeItem(this.KEY_USER);
  },

  isLoggedIn() {
    return this.getUser() !== null;
  },

  getRole() {
    const user = this.getUser();
    return user ? user.role : null;
  },

  getToken() {
    const user = this.getUser();
    return user ? user.token : null;
  }
};


(function() {
    const LOGIN_PAGE = '/login.html';
    const TOKEN_KEY  = 'authToken'; // <— change this if you store your JWT under a different key
  
    /**
     * Decode a JWT payload.
     * @param {string} token – the full JWT string
     * @returns {object} the parsed payload
     */
    function parseJwt(token) {
      try {
        const base64Url = token.split('.')[1];
        const base64    = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(
          atob(base64)
            .split('')
            .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
            .join('')
        );
        return JSON.parse(jsonPayload);
      } catch (e) {
        console.error('Failed to parse JWT:', e);
        return null;
      }
    }
  
    /**
     * Redirect to login page and optionally clear the token.
     */
    function redirectToLogin() {
      localStorage.removeItem(TOKEN_KEY);
      window.location.replace(LOGIN_PAGE);
    }
  
    // Don't run on the login page itself (avoids redirect loops)
    if (window.location.pathname === LOGIN_PAGE) {
      return;
    }
  
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token) {
      // no token at all → send to login
      return redirectToLogin();
    }
  
    const payload = parseJwt(token);
    if (
      !payload ||
      typeof payload.exp !== 'number' ||
      (Date.now() / 1000) >= payload.exp
    ) {
      // token malformed or expired → clear it and go to login
      return redirectToLogin();
    }
  
    // token is present and still valid → allow page to load
  })();
  
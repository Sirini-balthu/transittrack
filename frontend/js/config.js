// Backend location.
// - Running locally (npm run dev on port 5173): backend is http://localhost:8080
// - Deployed (frontend served by the backend itself): same address, so empty.
window.TT_API_BASE = window.TT_API_BASE !== undefined
  ? window.TT_API_BASE
  : (location.port === "5173" || location.port === "5500" ? "http://localhost:8080" : "");

// Route every relative API call ("/auth/login", "/routes", ...) to the backend
// and send the session cookie along with it.
(function () {
  const realFetch = window.fetch.bind(window);
  window.fetch = function (input, init) {
    init = Object.assign({}, init);
    if (typeof input === "string" && input.startsWith("/")) {
      input = window.TT_API_BASE + input;
      init.credentials = "include";
    }
    return realFetch(input, init);
  };
})();

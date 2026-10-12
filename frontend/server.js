// Tiny static file server - no dependencies, no downloads, no native files.
const http = require('http');
const fs = require('fs');
const path = require('path');
const { exec } = require('child_process');

const PORT = process.env.PORT || 5173;
const ROOT = __dirname;
const TYPES = {
  '.html': 'text/html; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.js': 'text/javascript; charset=utf-8',
  '.json': 'application/json',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon',
  '.txt': 'text/plain; charset=utf-8',
};

const server = http.createServer((req, res) => {
  let urlPath = decodeURIComponent(req.url.split('?')[0]);
  if (urlPath === '/') urlPath = '/index.html';
  const file = path.normalize(path.join(ROOT, urlPath));
  const blocked = ['server.js', 'package.json', 'node_modules'];
  if (!file.startsWith(ROOT) || blocked.some((b) => file.startsWith(path.join(ROOT, b)))) {
    res.writeHead(403); return res.end('Forbidden');
  }
  fs.readFile(file, (err, data) => {
    if (err) { res.writeHead(404, { 'Content-Type': 'text/plain' }); return res.end('Not found: ' + urlPath); }
    res.writeHead(200, {
      'Content-Type': TYPES[path.extname(file).toLowerCase()] || 'application/octet-stream',
      'Cache-Control': 'no-store',
    });
    res.end(data);
  });
});

server.on('error', (e) => {
  console.error(e.code === 'EADDRINUSE'
    ? 'Port ' + PORT + ' is already in use. Close the other frontend window and try again.'
    : e);
  process.exit(1);
});

server.listen(PORT, () => {
  const url = 'http://localhost:' + PORT;
  console.log('\n  TransitTrack frontend running at ' + url + '\n  (make sure the backend is running on http://localhost:8080)\n');
  const cmd = process.platform === 'win32' ? 'start "" "' + url + '"'
            : process.platform === 'darwin' ? 'open ' + url : 'xdg-open ' + url;
  exec(cmd, () => {});
});

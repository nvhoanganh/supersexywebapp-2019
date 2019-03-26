var express = require('express');
var HashRing = require('hashring');

var app = express();
var topics = [];

for (let index = 0; index < 100; index++) {
  topics.push('topic_' + index);
}
var ring = new HashRing(topics);

app.listen(3000, () => {
  console.log('Server running on port 3000');
});

app.get('/scan', (req, res, next) => {
  console.log('dropping request for URL: ' + req.query.url);
  var topic = ring.get(req.query.url);
  res.json({
    topicName: topic
  });
});

app.get('/healthz', nocache, (req, res) => {
  res.send('OK');
});

function nocache(req, res, next) {
  res.header('Cache-Control', 'private, no-cache, no-store, must-revalidate');
  res.header('Expires', '-1');
  res.header('Pragma', 'no-cache');
  next();
}

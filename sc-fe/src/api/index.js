export const api = {
  search: function(query) {
    return fetch(`http://localhost:8080/search?q=${query}`)
      .then(response => response.json())
      .then(json => json.results);
  },

  feed: function() {
    return fetch(`http://localhost:8080/feed`)
      .then(response => response.json())
      .then(json => json.feedItems);
  },

  analyzeLmp: function(lmp) {
    return fetch(`http://localhost:8080/demo/analyze`, {
      method: 'POST',
      body: lmp
    }).then(response => response.json())
  },

  submitRun: function(lmpData) {
    console.log(lmpData);
    return fetch(`http://localhost:8080/demo/submit`, {
      method: 'POST',
      body: lmpData,
    }).then(response => response.json())
  }
};
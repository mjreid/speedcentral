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
  }
};
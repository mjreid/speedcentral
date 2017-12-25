export const api = {
  search: function(query) {
    return fetch(`http://localhost:8080/search?q=${query}`)
      .then(response => response.json())
      .then(json => json.results.map(child => tempGen(child)));
  },

  feed: function() {
    return fetch(`http://localhost:8080/feed`)
      .then(response => response.json())
      .then(json => json.results);
  }
};

function tempGen(data) {
  return { "id": data.id, "name": data.name };
}
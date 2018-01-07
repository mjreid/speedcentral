export const api = {
  search: function(query) {
    return fetch(`${getBaseUrl()}/search?q=${query}`)
      .then(response => response.json())
      .then(json => json.results);
  },

  feed: function() {
    return fetch(`${getBaseUrl()}/feed`)
      .then(response => response.json())
      .then(json => json.feedItems);
  },

  analyzeLmp: function(lmp) {
    return fetch(`${getBaseUrl()}/demo/analyze`, {
      method: 'POST',
      body: lmp
    }).then(response => response.json());
  },

  submitRun: function(lmpData) {
    return fetch(`${getBaseUrl()}/demo/submit`, {
      method: 'POST',
      body: lmpData,
    }).then(response => response.json());
  },

  getRunStatusRequest: function(runId) {
    return fetch(`${getBaseUrl()}/demo/status?runId=${runId}`)
      .then(response => response.json())
      .then(json => json.run);
  },

  resolvePwad: function(pwadFilename, iwad) {
    return fetch(`${getBaseUrl()}/pwad/resolve?pwadFilename=${pwadFilename}&iwad=${iwad}`)
      .then(response => response.json());
  }
};

function getBaseUrl() {
  if (process.env.NODE_ENV === 'production') {
    return 'unknown'
  } else {
    return 'http://localhost:8080'
  }
}

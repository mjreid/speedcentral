
export const UPDATE_ROUTER_STATE = 'UPDATE_ROUTER_STATE';
export const SEARCH_REQUEST = 'SEARCH_REQUEST';
export const SEARCH_SUCCESS = 'SEARCH_SUCCESS';
export const SEARCH_FAILURE = 'SEARCH_FAILURE';

export const NAVIGATE = 'NAVIGATE';

function action(type, payload = {}) {
  return { type, ...payload };
}

export const navigate = pathname => action(NAVIGATE, { pathname });

export const search = {
  request: query => action(SEARCH_REQUEST, { query }),
  success: (query, response) => action(SEARCH_SUCCESS, { query, response }),
  failure: (query, error) => action(SEARCH_FAILURE, { query, error }),
};
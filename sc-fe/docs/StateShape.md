# sc-fe State Shape

## Motivation

It's encouraged to be very aware of what your state object looks like in Redux, so I'm keeping track of it here.

## Shape

```json
{
  "router": {
    "pathname": "/"
  },
  "searchResults": {
    "query": "foo", // so we know if we should clear out the search results or not 
    "paging": {
      "currentPage": 2,
      "pageSize": 10
    },
    "results": [
      {
        "id": 123,
        "name": "Super Mario Odyssey"
      }, {
        "id": 234,
        "name": "Bubsy 3D"
      }
    ]
  }
}
```
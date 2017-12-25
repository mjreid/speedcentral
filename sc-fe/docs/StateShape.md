# sc-fe State Shape

## Motivation

It's encouraged to be very aware of what your state object looks like in Redux, so I'm keeping track of it here.

However this is also probably totally inaccurate w/r/t the correct property names. But at least it's a place to organize my thoughts.

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
        "id": "123",
        "name": "Super Mario Odyssey"
      }, {
        "id": "234",
        "name": "Bubsy 3D"
      }
    ]
  },
  "newsfeed": {
    "feed": [{
        "itemType": "message",
        "id": "1",
        "date": "2017-12-14T00:29:40.276Z",
        "data": {
          "header": "Hello",
          "content": "Welcome to this speedrun site or whatever."
        }
      }, {
        "itemType": "speedrun",
        "id": "2",
        "date": "2017-12-17T12:20:40.276Z",
        "data": {
          "runner": "Leeroy Jenkins",
          "runnerId": "4444",
          "game": "Super Mario Odyssey",
          "gameId": "123",
          "runId": "BJ31J3",
          "runTime": "PT10M12.345S"
        }
      }, {
        "itemType": "highlight",
        "id": "3",
        "date": "2017-12-20T12:20:40.276Z",
        "data": {
          "runner": "Charlie Murphy",
          "runnerId": "5Z1234Z",
          "game": "Bubsy 3D",
          "gameId": "432",
          "runId": "SDFZSDF",
          "runTime": "PT8H10M55.543S",
          "videoUrl": "https://www.youtube.com/watch?v=cll8NYl088U"
        }
      }],
    "next": "100" // Next page token
  }
}
```
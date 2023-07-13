# Game Deals

## Installation

1. Clone the repo

```
git clone git@github.com:codyuhi/game-deals.git && cd game-deals
```

2. Build the Docker image

```
docker build -t game-deals .
```

3. Run the Docker container and expose a port for you to access from localhost

```
docker run -p 80:80 game-deals
```

4. Open a browser window and navigate to localhost and the port you opened
   "http://localhost:80"

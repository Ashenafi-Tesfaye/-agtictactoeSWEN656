let socket;
let gameId;
let playerMark = 'X';
let joinUrl = '';

function connectWebSocket() {
    if (socket && socket.readyState === WebSocket.OPEN) {
        console.log("You are already connected to a game.");
        return;
    }

    socket = new WebSocket("ws://localhost:8080/tictactoe/ws");

    socket.onopen = function() {
        console.log("Connected to WebSocket server.");
        const params = new URLSearchParams(window.location.search);
        gameId = params.get("gameId");
        playerMark = params.get("playerMark");
        if (!gameId || !playerMark) {
            console.log("gameId or playerMark is null");
            return;
        }
        socket.send(JSON.stringify({ type: "join", gameId: gameId, playerMark: playerMark }));

        const cells = document.querySelectorAll("td");
        cells.forEach(cell => {
            cell.addEventListener("click", function() {
                const row = this.getAttribute("data-row");
                const col = this.getAttribute("data-col");
                if (this.textContent === '-' && socket.readyState === WebSocket.OPEN) {
                    this.textContent = playerMark;
                    socket.send(JSON.stringify({ type: "move", gameId: gameId, playerMark: playerMark, row: row, col: col }));
                    playerMark = playerMark === 'X' ? 'O' : 'X'; // Switch playerMark after each move
                }
            });
        });

        // Display the game board
        document.getElementById('welcome-message').style.display = 'none';
        document.getElementById('board').style.display = 'table';
    };

 socket.onmessage = function(event) {
    const message = JSON.parse(event.data);
    if (message.type === "move") {
        const cell = document.querySelector(`td[data-row='${message.row}'][data-col='${message.col}']`);
        cell.textContent = message.playerMark;
    } else if (message.type === "error") {
        alert(message.error);
    } else if (message.type === "gameOver") {
        const winner = message.winner;
        announceWinner(winner);
    }

    handleServerMessage(message);
};

    socket.onclose = function() {
        console.log("Disconnected from WebSocket server.");
    };
}

function announceWinner(winner) {
    if (winner) {
        alert("Game Over: Player " + winner + " wins!");
    } else {
        alert("Game Over: It's a tie!");
    }
}

function handleServerMessage(message) {
    if (message.type === "update") {
        updateBoard(message.board);
        if (message.gameOver) {
            if (message.winner) {
                showWinnerMessage(message.winner);
            } else {
                showTieMessage();
            }
            // Reset the board and game state after a short delay
            setTimeout(resetGameState, 3000);
        }
    }
}

function updateBoard(board) {
    for (let row = 0; row < 3; row++) {
        for (let col = 0; col < 3; col++) {
            const cell = document.querySelector(`td[data-row="${row}"][data-col="${col}"]`);
            cell.textContent = board[row][col];
        }
    }
}

function startNewGame() {
    fetch('http://localhost:8080/tictactoe/start')
        .then(response => response.json())
        .then(data => {
            gameId = data.gameId;
            playerMark = data.playerMark;
            joinUrl = `${window.location.origin}/?gameId=${gameId}&playerMark=${playerMark}`;

            console.log('Game started:', data);

            // Display the game board
            document.getElementById('welcome-message').style.display = 'none';
            document.getElementById('board').style.display = 'table';

            // Update the URL parameters
            const newUrl = `${window.location.origin}${window.location.pathname}?gameId=${gameId}&playerMark=${playerMark}`;
            window.history.pushState({ path: newUrl }, '', newUrl);

            // Connect to WebSocket
            connectWebSocket();
        })
        .catch(error => {
            console.error('Error starting new game:', error);
        });
}

function copyGameLink() {
    if (joinUrl) {
        navigator.clipboard.writeText(joinUrl).then(() => {
            alert('Game link copied to clipboard');
        }).catch(err => {
            console.error('Could not copy text: ', err);
        });
    } else {
        alert('Start a new game first to generate the game link.');
    }
}

function sendGameLinkByEmail() {
    if (joinUrl) {
        const subject = 'Join my Tic-Tac-Toe game!';
        const body = `Hi,\n\nJoin me in a game of Tic-Tac-Toe! Click the link below to join the game:\n\n${joinUrl}\n\nBest regards,\nYour friend`;
        window.location.href = `mailto:?subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`;
    } else {
        alert('Start a new game first to generate the game link.');
    }
}

function showWinnerMessage(winner) {
    const announcement = document.getElementById('announcement');
    announcement.textContent = "Player " + winner + " wins!";
    announcement.className = 'message winner-message';
    announcement.style.display = 'block';
}

function showTieMessage() {
    const announcement = document.getElementById('announcement');
    announcement.textContent = "It's a tie!";
    announcement.className = 'message tie-message';
    announcement.style.display = 'block';
}

function resetGameState() {
    const cells = document.querySelectorAll("td");
    cells.forEach(cell => {
        cell.textContent = '-';
    });
    document.getElementById('welcome-message').style.display = 'block';
    document.getElementById('board').style.display = 'none';
    document.getElementById('announcement').style.display = 'none';
}

document.addEventListener("DOMContentLoaded", function() {
    const params = new URLSearchParams(window.location.search);
    gameId = params.get("gameId");
    playerMark = params.get("playerMark");

    // If gameId and playerMark are present in the URL, connect to WebSocket
    if (gameId && playerMark) {
        connectWebSocket();
    }

    // Setup event listeners for the game board if the player started the game
    const cells = document.querySelectorAll("td");
    cells.forEach(cell => {
        cell.addEventListener("click", function() {
            const row = this.getAttribute("data-row");
            const col = this.getAttribute("data-col");
            if (this.textContent === '-' && !gameId) {
                this.textContent = playerMark;
            }
        });
    });
});



// Global variables
let socket;
let gameId;
let currentPlayer; // Will be determined by the backend
let joinUrl = '';

// Function to connect WebSocket
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

        if (!gameId) {
            console.log("gameId is null");
            return;
        }

        // Generate a unique identifier for this tab if it doesn't exist
        let tabId = sessionStorage.getItem('tabId');
           if (!tabId) {
            tabId = Math.random().toString(36).substring(2) + Date.now();
            sessionStorage.setItem('tabId', tabId);
        }

        // Send join request to server with tabId
        socket.send(JSON.stringify({ type: "join", gameId: gameId, tabId: tabId }));

        // Display the game board
        document.getElementById('welcome-message').style.display = 'none';
        document.getElementById('board').style.display = 'table';
    };

    socket.onmessage = function(event) {
        const message = JSON.parse(event.data);
        console.log("Received message:", message); // Log the received message
    
        if (message.type === "join") {
            if (message.status === "success") {
            currentPlayer = message.playerMark; // Update currentPlayer from backend
            console.log(`Player mark set to ${currentPlayer}`);
            const board = message.board; // Assuming backend sends the initial board state
            updateBoard(board); // Update board with the current state
            } else {
                alert("Failed to join the game.");
            }
        } else if (message.type === "update") {
	        const board = message.board;
	        updateBoard(board); // Update board with the current state
	
	        // Re-enable the game board
	        const cells = document.querySelectorAll("td.cell-style");
	        cells.forEach(cell => {
            	cell.addEventListener("click", handleCellClick);
        	});
        } else if (message.type === "gameOver") {
            const winner = message.winner;
              // Enable the "Start New Game" button
        	document.getElementById('startGameButton').disabled = false;
            if (winner) {
                announceWinner(winner);
            } else {
                showTieMessage();
            }
        
        	setTimeout(() => {
                sessionStorage.clear(); // Clear session storage
                localStorage.clear();
                window.location.href = window.location.origin; // Redirect to base URL
            }, 2000); // Wait for 5 seconds before redirecting and clearing session storage
        } else if (message.type === "error") {
            alert(message.error);
        }
    };
    

    socket.onclose = function() {
        console.log("Disconnected from WebSocket server.");
    };
}

function startNewGame() {
    fetch(`http://localhost:8080/tictactoe/start`, {
        method: 'POST' // Specify the POST method
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to start a new game');
            }
            return response.json();
        })
        .then(data => {
            gameId = data.gameId;
            joinUrl = `${window.location.origin}/?gameId=${gameId}`;

            console.log('Game started:', data);

            // Display the game board
            document.getElementById('welcome-message').style.display = 'none';
            document.getElementById('board').style.display = 'table';

            // Update the URL parameters
            const newUrl = `${window.location.origin}${window.location.pathname}?gameId=${gameId}`;
            window.history.pushState({ path: newUrl }, '', newUrl);

            resetGameState(); // Reset the board state
            
             // Generate a new unique identifier for this tab
			 // Generate a new unique identifier for this tab
			let tabId = Math.random().toString(36).substring(2) + gameId;
			sessionStorage.setItem('tabId', tabId);

		 // Disable the "Start New Game" button
		    document.getElementById('startGameButton').disabled = true;
		    
            // Connect to WebSocket
            connectWebSocket();
        })
        .catch(error => {
            console.error('Error starting new game:', error);
            alert('Failed to start a new game. Please try again.');
        });
}

// Function to copy game link to clipboard
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

// Function to send game link via email
function sendGameLinkByEmail() {
    if (joinUrl) {
        const subject = 'Join my Tic-Tac-Toe game!';
        const body = `Hi,\n\nJoin me in a game of Tic-Tac-Toe! Click the link below to join the game:\n\n${joinUrl}\n\nBest regards,\nYour friend`;
        window.location.href = `mailto:?subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`;
    } else {
        alert('Start a new game first to generate the game link.');
    }
}

// Function to handle cell click
function handleCellClick(row, col) {
    console.log("Cell clicked:", row, col, " - Socket ready:", socket.readyState);
    // Check if row and col are defined and cell is empty and WebSocket is open before sending move
    if (row !== undefined && col !== undefined) {
        const cell = document.querySelector(`td.cell-style[data-row='${row}'][data-col='${col}']`);
        if (socket.readyState === WebSocket.OPEN && currentPlayer && cell && cell.textContent === '-') {
            // Send move request to server
            socket.send(JSON.stringify({ type: "move", gameId: gameId, playerMark: currentPlayer, row: row, col: col }));
        }
    }
}
// Function to announce winner
function announceWinner(winner) {
    const announcement = document.getElementById('announcement');
    announcement.textContent = "Player " + winner + " wins!";
    announcement.className = 'message winner-message';
    announcement.style.display = 'block';
}

// Function to show tie message
function showTieMessage() {
    const announcement = document.getElementById('announcement');
    announcement.textContent = "It's a tie!";
    announcement.className = 'message tie-message';
    announcement.style.display = 'block';
}

// Function to reset game state
function resetGameState() {
    const cells = document.querySelectorAll("td.cell-style");
    cells.forEach(cell => {
        cell.textContent = '-';
    });
    document.getElementById('announcement').style.display = 'none'; // Keep announcements hidden
}

// Function to update game board
function updateBoard(board) {
    for (let row = 0; row < 3; row++) {
        for (let col = 0; col < 3; col++) {
            const cell = document.querySelector(`td.cell-style[data-row='${row}'][data-col='${col}']`);
            if (cell) {
                cell.textContent = board[row][col];
            }
        }
    }
}

// Initialization when DOM is loaded
document.addEventListener("DOMContentLoaded", function() {
    const params = new URLSearchParams(window.location.search);
    gameId = params.get("gameId");

    // If gameId is present in the URL, connect to WebSocket
    if (gameId) {
        connectWebSocket();
    }

    // Setup event listeners for the game board cells
    const cells = document.querySelectorAll("td.cell-style");
    cells.forEach(cell => {
        cell.addEventListener("click", function() {
            const row = this.getAttribute("data-row");
            const col = this.getAttribute("data-col");
            handleCellClick(row, col);
        });
    });
    
      // Get the "Start New Game", "Copy Game Link" and "Email Game Link" buttons
    const startNewGameButton = document.getElementById('startGameButton');
    const copyGameLinkButton = document.getElementById('copyLinkButton');
    const emailGameLinkButton = document.getElementById('emailLinkButton');

    // Initially disable the "Copy Game Link" and "Email Game Link" buttons
    copyGameLinkButton.disabled = true;
    emailGameLinkButton.disabled = true;
    
     // Add event listener to the "Start New Game" button
    startNewGameButton.addEventListener('click', function() {
        // Enable the "Copy Game Link" and "Email Game Link" buttons when the "Start New Game" button is clicked
        copyGameLinkButton.disabled = false;
        emailGameLinkButton.disabled = false;
    });
});

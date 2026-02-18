// Task 6: Multiplayer Lobby – Array Generics
// Concepts: Generic Arrays

function createLobby<T>(players: T[]): { totalPlayers: number; players: T[] } {
    return {
        totalPlayers: players.length,
        players: players
    };
}

console.log("Task 6: Multiplayer Lobby – Array Generics");
console.log(createLobby(["Player1", "Player2", "Player3"]));
console.log(createLobby([{ name: "Alice", rank: 1 }, { name: "Bob", rank: 2 }]));

// Task 10: Game Leaderboard – Readonly Array + Generics
// Concepts: readonly arrays + Generics

function sortLeaderboard<T extends { score: number }>(players: readonly T[]): T[] {
    let copy = players.slice();
    for (let i = 0; i < copy.length - 1; i++) {
        for (let j = 0; j < copy.length - 1 - i; j++) {
            if (copy[j].score < copy[j + 1].score) {
                let temp = copy[j];
                copy[j] = copy[j + 1];
                copy[j + 1] = temp;
            }
        }
    }
    return copy;
}

const leaderboard = [
    { name: "Alex", score: 120 },
    { name: "Mira", score: 200 }
] as const;

console.log("Task 10: Game Leaderboard – Readonly Array + Generics");
console.log("Sorted:", sortLeaderboard(leaderboard));
console.log("Original (unchanged):", leaderboard);

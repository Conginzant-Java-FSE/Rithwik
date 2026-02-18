// Task 8: Esports Stats – Pick Utility
// Concepts: Pick<T, K>

interface PlayerStats {
    name: string;
    goals: number;
    assists: number;
    yellowCards: number;
}

function getOffensiveStats(stats: PlayerStats): Pick<PlayerStats, "goals" | "assists"> {
    return {
        goals: stats.goals,
        assists: stats.assists
    };
}

console.log("Task 8: Esports Stats – Pick Utility");
console.log(getOffensiveStats({
    name: "Ronaldo",
    goals: 3,
    assists: 1,
    yellowCards: 2
}));

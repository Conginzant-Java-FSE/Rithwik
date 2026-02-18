// Task 1: Esports Tournament – Readonly Team Roster
// Concepts: readonly interface

interface Team {
    readonly id: number;
    readonly name: string;
    players: string[];
    rank: number;
}

function updateRank(team: Team, newRank: number): Team {
    return { ...team, rank: newRank };
}

const team: Team = {
    id: 101,
    name: "CyberTitans",
    players: ["Ryu", "Blaze", "Nova"],
    rank: 5
};

console.log("Task 1: Esports Tournament – Readonly Team Roster");
const updatedTeam = updateRank(team, 2);
console.log("Updated Team:", updatedTeam);
console.log("Original Team (unchanged):", team);

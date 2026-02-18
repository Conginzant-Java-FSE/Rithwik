// Problem 6: Gaming Tournament Teams
interface TournamentTeam {
    readonly id: number;
    name: string;
    players: string[];
    points: number;
}

function addPoints(team: TournamentTeam, points: number): TournamentTeam {
    team.points = team.points + points;
    return team;
}

console.log("Problem 6: Gaming Tournament Teams");
let tournamentTeams: TournamentTeam[] = [
    { id: 1, name: "Phoenix", players: ["Alice", "Bob", "Carol"], points: 10 },
    { id: 2, name: "Dragons", players: ["Dave", "Eve", "Frank"], points: 8 },
    { id: 3, name: "Wolves", players: ["Grace", "Hank", "Ivy"], points: 12 }
];
console.log("Before:", tournamentTeams[0]);
addPoints(tournamentTeams[0], 5);
console.log("After:", tournamentTeams[0]);

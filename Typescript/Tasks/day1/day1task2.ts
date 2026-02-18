// Problem 2: Video Game Player Profile
type Rank = "Bronze" | "Silver" | "Gold" | "Platinum";

type Player = {
    username: string;
    level: number;
    experiencePoints: number;
    rank: Rank;
};

function promotePlayer(player: Player): Player {
    player.level = player.level + 1;
    player.experiencePoints = player.experiencePoints + 1000;
    return player;
}

console.log("Problem 2: Video Game Player Profile");
let player1: Player = { username: "ShadowKnight", level: 5, experiencePoints: 3000, rank: "Silver" };
let player2: Player = { username: "DragonSlayer", level: 10, experiencePoints: 8000, rank: "Gold" };
console.log("Before promotion:", player1);
player1 = promotePlayer(player1);
console.log("After promotion:", player1);
console.log("Before promotion:", player2);
player2 = promotePlayer(player2);
console.log("After promotion:", player2);

// Problem 1: FIFA Match Result System
interface Match {
    homeTeam: string;
    awayTeam: string;
    homeScore: number;
    awayScore: number;
    stadium: string;
    matchDate: Date;
    isFinished: boolean;
}

function getMatchResult(match: Match): string {
    if (!match.isFinished) {
        return "Match not completed";
    }
    if (match.homeScore > match.awayScore) {
        return match.homeTeam;
    } else if (match.awayScore > match.homeScore) {
        return match.awayTeam;
    } else {
        return "Draw";
    }
}

console.log("Problem 1: FIFA Match Result System");
let match1: Match = { homeTeam: "Brazil", awayTeam: "Argentina", homeScore: 3, awayScore: 1, stadium: "Maracana", matchDate: new Date("2025-06-15"), isFinished: true };
let match2: Match = { homeTeam: "Spain", awayTeam: "Germany", homeScore: 2, awayScore: 2, stadium: "Santiago Bernabeu", matchDate: new Date("2025-07-20"), isFinished: true };
let match3: Match = { homeTeam: "France", awayTeam: "Italy", homeScore: 0, awayScore: 0, stadium: "Stade de France", matchDate: new Date("2025-08-10"), isFinished: false };
console.log(getMatchResult(match1));
console.log(getMatchResult(match2));
console.log(getMatchResult(match3));
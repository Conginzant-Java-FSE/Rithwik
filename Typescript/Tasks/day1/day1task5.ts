// Problem 5: Startup Basic Information
interface Startup {
    name: string;
    foundedYear: number;
    totalFunding: number;
    isPublic: boolean;
}

function isEstablished(startup: Startup): boolean {
    if (startup.foundedYear < 2015) {
        return true;
    }
    return false;
}

console.log("Problem 5: Startup Basic Information");
let startup1: Startup = { name: "TechNova", foundedYear: 2010, totalFunding: 5000000, isPublic: true };
let startup2: Startup = { name: "CloudByte", foundedYear: 2020, totalFunding: 1000000, isPublic: false };
console.log(startup1.name + " is established: " + isEstablished(startup1));
console.log(startup2.name + " is established: " + isEstablished(startup2));

// Problem 4: Game Store Product Filter
type Platform = "PC" | "PlayStation" | "Xbox" | "Mobile";

interface StoreGame {
    title: string;
    price: number;
    platform: Platform;
    inStock: boolean;
}

function filterByPlatform(storeGames: StoreGame[], platform: Platform): StoreGame[] {
    let result: StoreGame[] = [];
    for (let i = 0; i < storeGames.length; i++) {
        if (storeGames[i].platform === platform && storeGames[i].inStock === true) {
            result.push(storeGames[i]);
        }
    }
    return result;
}

console.log("Problem 4: Game Store Product Filter");
let storeGames: StoreGame[] = [
    { title: "Cyber Quest", price: 59.99, platform: "PC", inStock: true },
    { title: "Battle Zone", price: 49.99, platform: "PlayStation", inStock: true },
    { title: "Speed Racer", price: 39.99, platform: "Xbox", inStock: false },
    { title: "Puzzle King", price: 9.99, platform: "Mobile", inStock: true },
    { title: "Dark Souls X", price: 69.99, platform: "PC", inStock: true }
];
console.log("PC games in stock:", filterByPlatform(storeGames, "PC"));

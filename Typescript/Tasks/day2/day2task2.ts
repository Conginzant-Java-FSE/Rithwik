// Task 2: Gaming Store – Generic Inventory Filter
// Concepts: Basic Generics

function filterAvailableItems<T extends { available: boolean }>(items: T[]): T[] {
    let result: T[] = [];
    for (let i = 0; i < items.length; i++) {
        if (items[i].available === true) {
            result.push(items[i]);
        }
    }
    return result;
}

const inventoryGames = [
    { title: "FIFA 25", available: true },
    { title: "CyberRacer", available: false }
];

console.log("Task 2: Gaming Store – Generic Inventory Filter");
console.log(filterAvailableItems(inventoryGames));

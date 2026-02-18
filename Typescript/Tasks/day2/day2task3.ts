// Task 3: Streaming App – Using keyof
// Concepts: keyof

function getPropertyValue<T, K extends keyof T>(obj: T, key: K): T[K] {
    return obj[key];
}

const movie = {
    title: "Galactic Wars",
    rating: 9.1,
    premium: true
};

console.log("Task 3: Streaming App – Using keyof");
console.log(getPropertyValue(movie, "rating"));
console.log(getPropertyValue(movie, "title"));
console.log(getPropertyValue(movie, "premium"));

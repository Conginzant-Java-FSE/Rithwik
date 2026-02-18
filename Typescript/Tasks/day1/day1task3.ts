// Problem 3: Mobile App Store Listing
interface MobileApp {
    name: string;
    developer: string;
    downloads: number;
    rating?: number;
    isPremium: boolean;
}

function isHighlyRated(app: MobileApp): boolean {
    if (app.rating !== undefined && app.rating >= 4.5) {
        return true;
    }
    return false;
}

console.log("Problem 3: Mobile App Store Listing");
let app1: MobileApp = { name: "InstaChat", developer: "ChatCo", downloads: 500000, rating: 4.8, isPremium: false };
let app2: MobileApp = { name: "PhotoEdit", developer: "PixelLab", downloads: 200000, rating: 3.9, isPremium: true };
let app3: MobileApp = { name: "QuickNote", developer: "NoteDev", downloads: 100000, isPremium: false };
console.log(app1.name + " highly rated: " + isHighlyRated(app1));
console.log(app2.name + " highly rated: " + isHighlyRated(app2));
console.log(app3.name + " highly rated: " + isHighlyRated(app3));

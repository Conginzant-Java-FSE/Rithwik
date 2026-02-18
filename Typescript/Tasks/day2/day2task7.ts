// Task 7: Mobile App Config – Using as const
// Concepts: as const

const config = {
    theme: "dark",
    version: 2
} as const;

type Theme = typeof config.theme;
let currentTheme: Theme = "dark";
console.log("Task 7: Mobile App Config – Using as const");
console.log("Current theme:", currentTheme);
console.log("Config:", config);

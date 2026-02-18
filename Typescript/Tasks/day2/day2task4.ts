// Task 4: Fintech Dashboard – Partial Profile Update
// Concepts: Partial<T>

interface UserProfile {
    name: string;
    balance: number;
    verified: boolean;
}

function updateProfile(profile: UserProfile, updates: Partial<UserProfile>): UserProfile {
    return { ...profile, ...updates };
}

const profile: UserProfile = {
    name: "Aarav",
    balance: 5000,
    verified: false
};

console.log("Task 4: Fintech Dashboard – Partial Profile Update");
const updatedProfile = updateProfile(profile, { verified: true });
console.log(updatedProfile);
console.log("Original profile (unchanged):", profile);

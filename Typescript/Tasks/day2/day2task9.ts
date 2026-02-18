// Task 9: SaaS Analytics – Omit Utility
// Concepts: Omit<T, K>

interface Account {
    id: number;
    email: string;
    password: string;
}

function sanitizeAccount(account: Account): Omit<Account, "password"> {
    return {
        id: account.id,
        email: account.email
    };
}

console.log("Task 9: SaaS Analytics – Omit Utility");
console.log(sanitizeAccount({
    id: 1,
    email: "user@app.com",
    password: "secret123"
}));

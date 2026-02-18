// Task 5: Fitness App – Required Subscription Upgrade
// Concepts: Required<T>

interface Subscription {
    plan?: string;
    expiryDate?: string;
}

function upgradeSubscription(sub: Subscription): Required<Subscription> {
    if (sub.plan === undefined || sub.expiryDate === undefined) {
        throw new Error("Both plan and expiryDate are required for upgrade.");
    }
    return { plan: sub.plan, expiryDate: sub.expiryDate };
}

console.log("Task 5: Fitness App – Required Subscription Upgrade");
console.log(upgradeSubscription({ plan: "Premium", expiryDate: "2026-01-01" }));

try {
    upgradeSubscription({ plan: "Basic" });
} catch (e: any) {
    console.log("Error:", e.message);
}

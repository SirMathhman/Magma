import express from "express";
import knex from "./db";

const app = express();
app.use(express.json());

app.get("/users", async (req, res) => {
    try {
        const users = await knex("users").select("*");
        res.json(users);
    } catch (error) {
        res.status(500).json({ error: "Database error" });
    }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});

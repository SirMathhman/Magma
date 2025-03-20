import bodyParser from "body-parser";
import cors from "cors";
import express from "express";
import knex, {Knex} from "knex";
import * as fs from "fs/promises";
import * as dotenv from "dotenv";
import * as paths from "path";

dotenv.config();

const envConfig: { [key: string]: Knex.Config } = {
    development: {
        client: "better-sqlite3",
        connection: {
            filename: "./database.sqlite",
        },
        useNullAsDefault: true
    }
};

const environment = process.env.NODE_ENV || "development";
const knexConfig = envConfig[environment];
const connection = knex(knexConfig);

const PORT = process.env.PORT || 3000;

const app = express();
app.use(express.json());
app.use(cors());
app.use(bodyParser.json());

app.get("/", async (req, res) => {
    const path = paths.join(process.cwd(), "src", "index.mgs"); // Adjust as needed
    const buffer = await fs.readFile(path, {encoding: "utf-8"});

    res.setHeader("Content-Type", "text/plain"); // ✅ Correct MIME type
    res.send(buffer);
});

app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}.`);
});

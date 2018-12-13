export const USER_API = "users";
export const AUTH_API = "auth";
export const THEATER_API = "kino/theaters";
export const SHOW_API = "kino/shows";
export const TICKET_API = "tickets";
export const MOVIE_API = "movies";

export const axios = require("axios").default.create({
    headers: { "X-Requested-With": "XMLHttpRequest"},
    withCredentials: true,
    baseURL: "http://bda1e937.ngrok.io/api/v1/"
});

import * as type from './action-types';
import axios from 'axios';

const URL = 'http://www.omdb.com/?apikey=55a2e515';

export function getMovie(movieImdbId) {
    return async (dispatch) => {
        axios.get(`${URL}&i=${movieImdbId}`).then(res => {
            dispatch({type: type.MOVIE_UPDATED, data: res})
        })
    }
}


/*
export function getRooms() {
    return async (dispatch) => {
        axios.get(`${URL}/games`).then(res => {
            dispatch({type: type.REFRESHED_LOBBY, data: {games: res.data.games}});
        });
    };
}

*/




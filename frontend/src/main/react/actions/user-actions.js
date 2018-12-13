import * as type from './action-types';
import axios from 'axios';
import {AUTH_API} from "../global";

const URL = '/users/';

export function login(username, password, history) {
    return async (dispatch) => {

        axios.post(`${AUTH_API}/login`, {username: username, password: password}).then(res => {
                dispatch({type: type.AUTHENTICATED, data: res});
                history.push('/');
            }).catch(() => {
            dispatch({type: type.AUTH_ERROR, data: 'Invalid email/password!'});
        });
    };
}

export function signUp(username, password, history) {
    return async (dispatch) => {
        axios.post(`${AUTH_API}/register`, {username: username, password: password}).then(res => {
            dispatch({type: type.AUTHENTICATED, data: res});
            history.push('/');
        }).catch((err) => {
            console.error(err);
            dispatch({type: type.AUTH_ERROR, data: 'Username is already in use!'});
        });
    };
}

export function logOut() {
    return async (dispatch) => {
        axios.post(`${AUTH_API}/logout`).then(() => {
            dispatch({type: type.NOT_AUTHENTICATED});
        }).catch((err) => {
            dispatch({type: type.AUTH_ERROR, data: `Unknown error occured: ${err}`});
        });
    };
}
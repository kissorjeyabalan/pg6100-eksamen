import * as type from '../actions/action-types';
import appState from './app-state';

export default function (state = appState.user, action) {
    switch(action.type) {
        case type.AUTHENTICATED:
            return { ...state, authenticated: true };
        case type.NOT_AUTHENTICATED:
            return { ...state, authenticated: false, userId: null };
        default:
            return state;
    }
}
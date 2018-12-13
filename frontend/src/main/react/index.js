import React from 'react';
import ReactDOM from 'react-dom';
import {Provider} from 'react-redux';
import {applyMiddleware, createStore} from 'redux';
import thunk from 'redux-thunk';
import {composeWithDevTools} from 'redux-devtools-extension';
import rootReducer from './reducers/root-reducer';
import {AUTHENTICATED, NOT_AUTHENTICATED} from './actions/action-types';
import App from './components/App';
import 'babel-polyfill';
import {AUTH_API, axios} from "./global";
import {logOut} from "./actions/user-actions";

const store = createStore(rootReducer, composeWithDevTools(
    applyMiddleware(thunk)
));

axios.get(`${AUTH_API}/user`).then(res => {
    store.dispatch({type: AUTHENTICATED, data: res.data});
}).catch(() => {
    store.dispatch(logOut())
});

ReactDOM.render(
    <Provider store={store}>
        <App/>
    </Provider>,
    document.getElementById('root')
);
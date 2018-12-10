import React from 'react';
import {BrowserRouter, Route, Switch} from 'react-router-dom';

import HomePage from './home/HomePage';
import LoginPage from './auth/LoginPage';
import RegisterPage from './auth/RegisterPage';
import HeaderBar from "./header/HeaderBar";


class App extends React.Component {
    render() {
        return (
            <BrowserRouter>
                <div>
                    <HeaderBar/>
                    <Switch>
                        <Route exact path="/moviepage"
                               render={props => <MoviePage {...props} userId={this.state.id}/>}/>
                        <Route exact path="/login" component={LoginPage} />
                        <Route exact path="/signup" component={RegisterPage} />
                        <Route exact path="/" component={HomePage}/>
                    </Switch>
                </div>
            </BrowserRouter>
        );
    }
}

export default App;
import React from 'react';
import {BrowserRouter, Route, Switch} from 'react-router-dom';

import HomePage from './home/HomePage';

class App extends React.Component {
    render() {
        return (
            <BrowserRouter>
                <div>
                    <Switch>
                        <Route exact path="/" component={HomePage}/>
                    </Switch>
                </div>
            </BrowserRouter>
        );
    }
}

export default App;
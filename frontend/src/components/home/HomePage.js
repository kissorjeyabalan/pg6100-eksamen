import React from 'react';
import {Link, withRouter} from 'react-router-dom';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';

class HomePage extends React.Component {

    render() {

        return(
            <div>
                <div>
                    <div>
                        <h2>Movie-Service</h2>
                        <p>See this weeks movies</p>
                    </div>
                </div>
            </div>
        );    
    }
}



function mapStateToProps(state) {
    return {

    };
}

export default withRouter(connect(mapStateToProps)(HomePage))


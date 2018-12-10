import React from 'react';
import {Link, withRouter} from 'react-router-dom';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import * as ApiBase from '../../global'
import axios from 'axios'

class MoviePage extends React.Component {

    constructor(props) {
        super(props)

    }

    componentDidMount() {

    }


    render() {
        let movieId = this.props.id

        return(
            <div>
                <div>
                    <div>
                        <h2>Movie-Detail-Page</h2>
                        <p>{movieId}</p>
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

export default withRouter(connect(mapStateToProps)(MoviePage))
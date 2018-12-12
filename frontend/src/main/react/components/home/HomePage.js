import React from 'react';
import {Link, withRouter} from 'react-router-dom';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import * as ApiBase from '../../global'
import axios from 'axios'

class HomePage extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            featuredMovies: null
        }
    }

    componentDidMount() {
        axios.get(`${ApiBase.MOVIE_API}?featured=true`).then(res => {
            let movies = res.data.data.data
            this.setState({featuredMovies: movies})
        })

    }

    render() {

        let featuredMovieList = <div></div>

        if(this.state.featuredMovies !== null){
            featuredMovieList = <div className="movie-list">
                {this.state.featuredMovies.map(m =>
                    <div key={m.id} className="movie-list-card">
                        <p className="movie-title" >Title: {m.title}</p>
                        <p className="movie-desce" >Description: {m.description}</p>
                    </div>
                )}
            </div>
        }

        return(
            <div className="container">
                <div className="header">
                    <h2>Movie-Service</h2>
                    <p>Here is a selection of this weeks featured movies</p>
                </div>
                {featuredMovieList}
            </div>
        );    
    }
}



function mapStateToProps(state) {
    return {

    };
}

export default withRouter(connect(mapStateToProps)(HomePage))


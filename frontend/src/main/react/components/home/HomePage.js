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
            let movies = res.data
            this.setState({featuredMovies: movies})
        })
    }



    render() {

        let featuredMovieList = <div></div>

        if(this.state.featuredMovies != null) {
            featuredMovieList = <div className="featured-movie-list">
                {this.state.featuredMovies.map(m => {
                    <div key={"key_" + m.id} className="featured-movie">
                        <p>m.title</p>
                        <Link to={`/moviepage/${ this.props.id }`}>Movie Details</Link>
                    </div>
                })}
            </div>
        }

        return(
            <div className="container">
                <div>
                    <h2>Movie-Service</h2>
                    <p>Here is a selection of this weeks featured movies</p>
                    {featuredMovieList}
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


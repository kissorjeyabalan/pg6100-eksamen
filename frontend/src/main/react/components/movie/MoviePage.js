import React from 'react';
import {Link, withRouter} from 'react-router-dom';
import {connect} from 'react-redux';
import axios from "axios/index";
import * as ApiBase from "../../global";

class MoviePage extends React.Component {

    constructor(props) {
        super(props)

        this.state = {
            movie: null,
            error: null
        };

        this.movieId = new URLSearchParams(window.location.search).get("movieId");

        if(this.movieId === null){
            this.state.error = "Unspecified book id";
        }

    }

    componentDidMount() {
        axios.get(`${ApiBase.MOVIE_API}/${this.movieId}`).then(res => {

            let payload = res.data.data
            console.log(payload)
            this.setState({movie: payload})
        })

    }


    render() {

        let movie = <div>Loading</div>

        if(this.state.movie !== null) {
            movie = <div className="movie-container">
                <h2>{this.state.movie.title}</h2>

                <p>Release Date: {this.state.movie.release_date}</p>
                <p>{this.state.movie.description}</p>
            </div>
        }

        return(
            <div className="container">
                <h2>Movie-Detail-Page</h2>
                {movie}
            </div>
        );
    }
}



function mapStateToProps(state) {
    return {

    };
}

export default withRouter(connect(mapStateToProps)(MoviePage))
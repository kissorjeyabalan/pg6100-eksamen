import React from 'react';
import {Link, withRouter} from 'react-router-dom';
import {connect} from 'react-redux';
import {MOVIE_API, SHOW_API, axios} from "../../global";

class MoviePage extends React.Component {

    constructor(props) {
        super(props)

        this.state = {
            movie: null,
            screenings: null,
            error: null
        };

        this.movieId = new URLSearchParams(window.location.search).get("movieId");

        if(this.movieId === null){
            this.state.error = "Unspecified book id";
        }
    }

    componentDidMount() {

        axios.get(`${MOVIE_API}/${this.movieId}`).then(res => {

            let payload = res.data.data
            console.log("movie api")
            console.log(payload)
            this.setState({movie: payload})
        })

        axios.get(`${SHOW_API}?movieId=${this.movieId}`).then(res => {

            let payload = res.data.data.data
            console.log("show api")
            console.log(payload)
            this.setState({screenings: payload})
        })
    }


    render() {

        let movie = <div>Loading movie</div>

        if(this.state.movie !== null) {
            movie = <div className="movie-container">
                <h2>{this.state.movie.title}</h2>

                <p>Release Date: {this.state.movie.release_date}</p>
                <p>{this.state.movie.description}</p>
            </div>
        }

        let screenings = <div>Loading screenings</div>

        if(this.state.screenings !== null) {
            screenings = <div className="container">
                {this.state.screenings.map( s =>
                    <div key={s.id}>
                        <p>Starttime: {s.startTime}</p>
                        <p>Available Seats: {s.availableSeats}</p>
                    </div>
                )}
            </div>
        }

        return(
            <div className="container">
                <h2>Movie-Detail-Page</h2>
                {movie}
                {screenings}
            </div>
        );
    }
}



function mapStateToProps(state) {
    return {

    };
}

export default withRouter(connect(mapStateToProps)(MoviePage))
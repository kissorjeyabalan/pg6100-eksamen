import React from 'react';
import {Link, withRouter} from 'react-router-dom';
import {connect} from 'react-redux';
import {MOVIE_API, axios} from "../../global";

class HomePage extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            featuredMovies: null
        }
    }

    componentDidMount() {
        axios.get(`${MOVIE_API}?featured=true`).then(res => {
            let movies = res.data.data.data;
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
                        <p className="movie-desc" >Description: {m.description}</p>
                        <Link to={"/moviepage?movieId=" + m.id}>
                            <button className="btn black">
                                Details
                            </button>
                        </Link>
                    </div>
                )}
            </div>
        }

        return(
            <div className="container">
                <div className="header">
                    <h2>Movie-Service</h2>
                    <p>Here is a selection of this weeks featured movies<br/> Click to see screening info for each movie</p>
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


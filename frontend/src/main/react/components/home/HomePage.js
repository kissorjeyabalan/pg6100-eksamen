import React from 'react';
import {Link, withRouter} from 'react-router-dom';
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
        /*
        axios.get(`${ApiBase.MOVIE_API}?featured=true`).then(res => {
            let movies = res.data.data.data;
            this.setState({featuredMovies: movies})
        })*/


        let fakeReply = {
            data: [
                {
                    id: 1,
                    title: "he-man",
                    description: "here is a loooooooooong description, dawdwada dwa d aw wda daw  dawd awdwad  d wd a w wd awd  dw"
                },
                {
                    id: 2,
                    title: "turtles",
                    description: "here is a loooooooooong description, dawdwada dwa d aw wda daw  dawd awdwad  d wd a w wd awd  dw"
                },
                {
                    id: 3,
                    title: "barbie",
                    description: "here is a loooooooooong description, dawdwada dwa d aw wda daw  dawd awdwad  d wd a w wd awd  dw"
                },
                {
                    id: 4,
                    title: "mega-man",
                    description: "here is a loooooooooong description, dawdwada dwa d aw wda daw  dawd awdwad  d wd a w wd awd  dw"
                },
            ]
        }
         this.setState({
            featuredMovies: fakeReply.data
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


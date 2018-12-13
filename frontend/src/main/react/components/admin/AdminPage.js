import React from 'react';
import {Link, withRouter} from 'react-router-dom';
import {connect} from 'react-redux';
import {axios, SHOW_API} from "../../global";

class AdminPage extends React.Component {

    constructor(props) {
        super(props)

        this.state = {
            shows: null,
            error: null
        }

        this.showList = this.showList.bind(this)
    }

    componentDidMount() {

        axios.get(`${SHOW_API}`).then(res => {

            let payload = res.data.data.data
            console.log(payload)
            this.setState({shows: payload})
            }
        )

    }

    showList() {
        let showList = <div></div>

        if (this.state.shows !== null && this.state.shows !== undefined) {
            showList = <div className="show-list">
                {this.state.shows.map(m =>
                    <div key={m.id} className="show-list-card">
                        <p className="show-title">Title: {m.movieId}</p>
                        <p className="show-cinema">Cinema: {m.cinemaId}</p>
                        <p className="show-time">Start time: {m.startTime}</p>
                        <p className="show-seats">Available seats: {m.availableSeats.length}</p>
                        <Link to={`/admin/EditShow/${m.id}`}>
                            <button className="btn black">
                                Edit
                            </button>
                        </Link>
                    </div>
                )}
            </div>
        }
        return showList
    }

    render() {

        var showList = this.showList()

        return(
            <div className="container">
                This is the admin area

                <h1>change time of shows</h1>
                <p>Create a screening</p>
                <p>Link to page create theater</p>
                <p>Link to page with all theaters and capabilties to edit them</p>
                <p>Link to page create show</p>
                <p>Link to page with all shows and capabilities to change them</p>
                <p>Link to page create movie</p>
                <p>x Link to page with all movies and capabilities to change them</p>
                <p>Link to page with all tickets</p>
                <p>Link to page with all reservations</p>
                <p>Link to page users</p>
                {showList}
            </div>
        );
    }
}



function mapStateToProps(state) {
    return {

    };
}

export default withRouter(connect(mapStateToProps)(AdminPage))


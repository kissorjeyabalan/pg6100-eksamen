import React from 'react';
import {Link, withRouter} from 'react-router-dom';
import {connect} from 'react-redux';
import * as ApiBase from '../../global'
import axios from 'axios'

class AdminPage extends React.Component {

    constructor(props) {
        super(props)
    }


    render() {
        return(
            <div className="container">
                This is the admin area
            </div>
        );
    }
}



function mapStateToProps(state) {
    return {

    };
}

export default withRouter(connect(mapStateToProps)(AdminPage))


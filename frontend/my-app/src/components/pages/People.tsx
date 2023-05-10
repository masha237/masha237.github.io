import React, {useEffect, useState} from 'react';
import {UserService} from "../../Utils/UserService";
import {Navigate} from "react-router-dom";
import IResponse from "../../types/IResponse";
import {IUser} from "../../types/IUser";
import Friend from "../molecules/Friend";
import {Jwt} from "../../types/Jwt";
import "../../styles/layout.css"
import "../../styles/friend.css"

interface Props {
    jwt0: Jwt,
    friends: Array<IUser> | null;
    setFriends: React.Dispatch<React.SetStateAction<IUser[] | null>>
}

const People = ({jwt0, setFriends}:Props) => {
    const login: string | null = localStorage.getItem("login");
    const [people, setPeople] = useState<Array<IUser> | null>()


    useEffect(() => {
        if (login === null) {
            return;
        }
        UserService.getPeople(login).then((response: IResponse<Array<IUser>>) => {
            if (response.error === null) {
                setPeople(response.data)
            }
        }).catch(console.log);
    }, []);

    if (login === null) {
        return <Navigate to={"../login"}/>;
    }
    return (
        <div className="content friends">
            {people?.map((user: IUser) => {
                console.log(user);
                return <Friend key={user.id} user={user} jwt0={jwt0} setFriends={setFriends}/>
            })}
        </div>
    );
};

export default People;
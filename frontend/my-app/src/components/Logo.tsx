import React from 'react';
import "./../styles/layout.css"
const Logo: React.FC = () => {
    return (
        <img id="vk-logo" src={process.env.PUBLIC_URL + "/Vkontakte.png"} alt={"Вконтакте"}/>
    );
};

export default Logo;
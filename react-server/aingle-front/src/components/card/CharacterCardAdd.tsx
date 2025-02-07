import { useNavigate } from "react-router-dom";

const CharacterCardAdd = () => {
  const navigate = useNavigate();
  const goCreate = () => {
    navigate("/vote/create");
  };

  return (
    <div className="relative w-[100px] h-[100px] mx-1 my-1" onClick={goCreate}>
      <div className="bg-white w-full h-full rounded-lg overflow-hidden flex justify-center items-center">
        <span className="text-3xl font-bold text-pink-500 transform scale-125 cursor-pointer">
          +
        </span>
      </div>
    </div>
  );
};

export default CharacterCardAdd;

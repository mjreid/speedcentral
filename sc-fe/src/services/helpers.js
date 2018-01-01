export function generateRunTitle(
  iwad,
  map,
  episode,
  runTime,
  runner,
  category
) {
  const dIwad = iwadProperName(iwad);
  const dCategory = categoryProperName(category);
  const dMap= mapProperName(iwad, map, episode);

  return `${dIwad} - ${dMap} (${dCategory}) in ${runTime} by ${runner}`;
}

export function iwadProperName(iwad) {
  if (iwad === "doom") return "DOOM";
  else if (iwad === "doom2") return "DOOM2";
  else return "";
}

export function categoryProperName(category) {
  if (category === "uv-max") return "UV Max";
  else if (category === "nm100") return "NM 100";
  else if (category === "uv-speed") return "UV Speed";
  else if (category === "nm-speed") return "NM Speed";
  else return "";
}

export function mapProperName(iwad, map, episode) {
  if (iwad === "doom") {
    return `E${episode}M${map}`;
  } else {
    // Add leading zero to single-digit maps
    if (map < 10) {
      return `MAP0${map}`;
    } else {
      return `MAP${map}`;
    }
  }
}
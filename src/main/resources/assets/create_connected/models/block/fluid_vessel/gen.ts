const files = [
    'block_x_single',
    'block_x_single_window',
    'block_x_single_window_top',
    'block_x_single_window_middle',
    'block_x_single_window_bottom',
    'block_x_positive',
    'block_x_positive_window',
    'block_x_positive_window_top',
    'block_x_positive_window_middle',
    'block_x_positive_window_bottom',
    'block_x_negative',
    'block_x_negative_window',
    'block_x_negative_window_top',
    'block_x_negative_window_middle',
    'block_x_negative_window_bottom',
    'block_x_middle',
    'block_x_middle_window',
    'block_x_middle_window_top',
    'block_x_middle_window_middle',
    'block_x_middle_window_bottom',
    'block_z_single',
    'block_z_single_window',
    'block_z_single_window_top',
    'block_z_single_window_middle',
    'block_z_single_window_bottom',
    'block_z_positive',
    'block_z_positive_window',
    'block_z_positive_window_top',
    'block_z_positive_window_middle',
    'block_z_positive_window_bottom',
    'block_z_negative',
    'block_z_negative_window',
    'block_z_negative_window_top',
    'block_z_negative_window_middle',
    'block_z_negative_window_bottom',
    'block_z_middle',
    'block_z_middle_window',
    'block_z_middle_window_top',
    'block_z_middle_window_middle',
    'block_z_middle_window_bottom',
];

const template = `{
    "parent": "create_connected:block/fluid_vessel/block_x_single_window"
}`

await Promise.all(files.map(async (file) => {
    if (await Bun.file(file + ".json").exists()) return;
    const isX = file.startsWith("block_x");
    await Bun.write(file + ".json", isX ? template : template.replace(/_x_/g, "_z_"));
}));